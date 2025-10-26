package com.mes.backend.controller;

import com.mes.backend.dto.request.CreateWorkOrderRequest;
import com.mes.backend.dto.request.UpdateWorkOrderStatusRequest;
import com.mes.backend.dto.response.WorkOrderResponse;
import com.mes.backend.entity.WorkOrderStatus;
import com.mes.backend.service.WorkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 작업지시 컨트롤러
 * Base URL: /api/work-orders
 */
@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
@Slf4j
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    /**
     * 전체 작업지시 목록 조회
     * GET /api/work-orders
     */
    @GetMapping
    public ResponseEntity<List<WorkOrderResponse>> getAllWorkOrders() {
        log.info("GET /api/work-orders - 전체 작업지시 목록 조회 요청");
        List<WorkOrderResponse> workOrders = workOrderService.getAllWorkOrders();
        log.info("전체 작업지시 목록 조회 성공: count={}", workOrders.size());
        return ResponseEntity.ok(workOrders);
    }

    /**
     * 작업지시 단건 조회 (ID)
     * GET /api/work-orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> getWorkOrder(@PathVariable Long id) {
        log.info("GET /api/work-orders/{} - 작업지시 조회 요청", id);
        WorkOrderResponse workOrder = workOrderService.getWorkOrderById(id);
        log.info("작업지시 조회 성공: id={}, orderNo={}",
                workOrder.getId(), workOrder.getOrderNo());
        return ResponseEntity.ok(workOrder);
    }

    /**
     * 작업지시 단건 조회 (지시번호)
     * GET /api/work-orders/order-no/{orderNo}
     */
    @GetMapping("/order-no/{orderNo}")
    public ResponseEntity<WorkOrderResponse> getWorkOrderByOrderNo(
            @PathVariable String orderNo
    ) {
        log.info("GET /api/work-orders/order-no/{} - 작업지시 조회 요청", orderNo);
        WorkOrderResponse workOrder = workOrderService.getWorkOrderByOrderNo(orderNo);
        log.info("작업지시 조회 성공: orderNo={}, productName={}",
                orderNo, workOrder.getProductName());
        return ResponseEntity.ok(workOrder);
    }

    /**
     * 상태별 작업지시 조회
     * GET /api/work-orders?status={status}
     */
    @GetMapping(params = "status")
    public ResponseEntity<List<WorkOrderResponse>> getWorkOrdersByStatus(
            @RequestParam WorkOrderStatus status
    ) {
        log.info("GET /api/work-orders?status={} - 상태별 작업지시 조회 요청", status);
        List<WorkOrderResponse> workOrders = workOrderService.getWorkOrdersByStatus(status);
        log.info("상태별 작업지시 조회 성공: status={}, count={}", status, workOrders.size());
        return ResponseEntity.ok(workOrders);
    }

    /**
     * 작업지시 생성
     * POST /api/work-orders
     */
    @PostMapping
    public ResponseEntity<WorkOrderResponse> createWorkOrder(
            @Valid @RequestBody CreateWorkOrderRequest request
    ) {
        log.info("POST /api/work-orders - 작업지시 생성 요청: orderNo={}",
                request.getOrderNo());
        WorkOrderResponse workOrder = workOrderService.createWorkOrder(request);
        log.info("작업지시 생성 성공: id={}, orderNo={}",
                workOrder.getId(), workOrder.getOrderNo());
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrder);
    }

    /**
     * 작업지시 상태 변경
     * PATCH /api/work-orders/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderResponse> updateWorkOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkOrderStatusRequest request
    ) {
        log.info("PATCH /api/work-orders/{}/status - 상태 변경 요청: newStatus={}",
                id, request.getStatus());
        WorkOrderResponse workOrder = workOrderService.updateWorkOrderStatus(id, request);
        log.info("작업지시 상태 변경 성공: id={}, status={}",
                workOrder.getId(), workOrder.getStatus());
        return ResponseEntity.ok(workOrder);
    }
}
