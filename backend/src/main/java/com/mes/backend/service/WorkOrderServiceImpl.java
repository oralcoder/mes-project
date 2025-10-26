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
        log.info("작업지시 생성 요청: orderNo={}, productId={}, quantity={}",
                request.getOrderNo(), request.getProductId(), request.getQuantity());

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
                .build();

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        log.info("작업지시 생성 완료: id={}, orderNo={}",
                savedWorkOrder.getId(), savedWorkOrder.getOrderNo());

        return WorkOrderResponse.from(savedWorkOrder);
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
