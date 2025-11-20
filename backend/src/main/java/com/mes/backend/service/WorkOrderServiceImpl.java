package com.mes.backend.service;

import com.mes.backend.dto.request.CreateWorkOrderRequest;
import com.mes.backend.dto.request.UpdateWorkOrderStatusRequest;
import com.mes.backend.dto.response.WorkOrderResponse;
import com.mes.backend.entity.Product;
import com.mes.backend.entity.WorkOrder;
import com.mes.backend.entity.WorkOrderStatus;
import com.mes.backend.exception.DuplicateResourceException;
import com.mes.backend.exception.ResourceNotFoundException;
import com.mes.backend.repository.ProductRepository;
import com.mes.backend.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

/**
 * 작업지시 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public WorkOrderResponse createWorkOrder(CreateWorkOrderRequest request) {
        log.info("작업지시 생성 요청: orderNo={}, productId={}, quantity={}, orderDate={} dueDate={}",
                request.getOrderNo(), request.getProductId(), request.getQuantity(), request.getOrderDate(), request.getDueDate());

        // 1. 지시번호 중복 체크
        if (workOrderRepository.existsByOrderNo(request.getOrderNo())) {
            log.warn("지시번호 중복: orderNo={}", request.getOrderNo());
            throw new DuplicateResourceException("작업지시", "지시번호", request.getOrderNo());
        }

        // 2. 제품 존재 확인
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> {
                    log.warn("제품을 찾을 수 없음: productId={}", request.getProductId());
                    return new ResourceNotFoundException("제품", "id", request.getProductId());
                });

        // 3. 작업지시 생성
        WorkOrder workOrder = WorkOrder.builder()
                .orderNo(request.getOrderNo())
                .product(product)
                .quantity(request.getQuantity())
                .status(WorkOrderStatus.PLANNED)  // 초기 상태는 계획
                .orderDate(request.getOrderDate())
                .dueDate(request.getDueDate())
                .build();

        // AI 예측 로직 추가 가능 (납기 준수 여부)
        boolean is_on_time = callAiOnTimePredictionApi(workOrder);
        log.info("AI 납기 준수 예측 결과: is_on_time={}", is_on_time);

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        log.info("작업지시 생성 완료: id={}, orderNo={}",
                savedWorkOrder.getId(), savedWorkOrder.getOrderNo());

        return WorkOrderResponse.from(savedWorkOrder);
    }
    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private boolean callAiOnTimePredictionApi(WorkOrder wo) {
    try {
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("productId", wo.getProduct().getId());
        payload.put("quantity", wo.getQuantity());
        payload.put("orderDate", wo.getOrderDate().format(DATETIME_FORMATTER));
        payload.put("dueDate", wo.getDueDate().format(DATETIME_FORMATTER));

        org.springframework.http.ResponseEntity<java.util.Map> resp =
                restTemplate.postForEntity("http://ai-server:8000/is_on_time", payload, java.util.Map.class);
        // ↑ URL/포트는 실제 환경에 맞춰 수정

        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            Object v = resp.getBody().get("is_on_time");  // FastAPI 응답 키와 맞추기
            if (v instanceof Boolean) {
                return (Boolean) v;
            }
        }

        log.warn("AI 응답 파싱 실패, 기본값 false 적용");
        return false;
    } catch (Exception e) {
        log.error("AI 예측 API 호출 실패", e);
        return false;
    }
}

    @Override
    public WorkOrderResponse getWorkOrderById(Long id) {
        log.info("작업지시 조회 요청: id={}", id);

        // Fetch Join으로 제품 정보 함께 로드
        WorkOrder workOrder = workOrderRepository.findByIdWithProduct(id)
                .orElseThrow(() -> {
                    log.warn("작업지시를 찾을 수 없음: id={}", id);
                    return new ResourceNotFoundException("작업지시", "id", id);
                });

        log.info("작업지시 조회 완료: id={}, orderNo={}",
                workOrder.getId(), workOrder.getOrderNo());

        return WorkOrderResponse.from(workOrder);
    }

    @Override
    public WorkOrderResponse getWorkOrderByOrderNo(String orderNo) {
        log.info("작업지시 조회 요청: orderNo={}", orderNo);

        // Fetch Join으로 제품 정보 함께 로드
        WorkOrder workOrder = workOrderRepository.findByOrderNoWithProduct(orderNo)
                .orElseThrow(() -> {
                    log.warn("작업지시를 찾을 수 없음: orderNo={}", orderNo);
                    return new ResourceNotFoundException("작업지시", "지시번호", orderNo);
                });

        log.info("작업지시 조회 완료: orderNo={}, productName={}",
                orderNo, workOrder.getProduct().getName());

        return WorkOrderResponse.from(workOrder);
    }

    @Override
    public List<WorkOrderResponse> getAllWorkOrders() {
        log.info("전체 작업지시 목록 조회 요청");

        // Fetch Join으로 N+1 문제 해결
        List<WorkOrder> workOrders = workOrderRepository.findAllWithProduct();

        log.info("작업지시 목록 조회 완료: count={}", workOrders.size());

        return workOrders.stream()
                .map(WorkOrderResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrderResponse> getWorkOrdersByStatus(WorkOrderStatus status) {
        log.info("상태별 작업지시 조회 요청: status={}", status);

        List<WorkOrder> workOrders = workOrderRepository.findByStatus(status);

        log.info("상태별 작업지시 조회 완료: status={}, count={}", status, workOrders.size());

        return workOrders.stream()
                .map(WorkOrderResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WorkOrderResponse updateWorkOrderStatus(Long id, UpdateWorkOrderStatusRequest request) {
        log.info("작업지시 상태 변경 요청: id={}, newStatus={}", id, request.getStatus());

        // 작업지시 조회
        WorkOrder workOrder = workOrderRepository.findByIdWithProduct(id)
                .orElseThrow(() -> {
                    log.warn("작업지시를 찾을 수 없음: id={}", id);
                    return new ResourceNotFoundException("작업지시", "id", id);
                });

        WorkOrderStatus oldStatus = workOrder.getStatus();
        WorkOrderStatus newStatus = request.getStatus();

        // 상태 전환
        try {
            switch (newStatus) {
                case IN_PROGRESS:
                    workOrder.start();
                    break;
                case COMPLETED:
                    workOrder.complete();
                    break;
                case CANCELLED:
                    workOrder.cancel();
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 상태입니다: " + newStatus);
            }
        } catch (IllegalStateException e) {
            log.warn("상태 전환 실패: id={}, oldStatus={}, newStatus={}", id, oldStatus, newStatus);
            throw new IllegalArgumentException(e.getMessage());
        }

        // 저장 (변경 감지로 자동 UPDATE)
        WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);

        log.info("작업지시 상태 변경 완료: id={}, {} → {}",
                id, oldStatus, updatedWorkOrder.getStatus());

        return WorkOrderResponse.from(updatedWorkOrder);
    }
}
