package com.mes.backend.service;

import com.mes.backend.dto.request.CreateWorkOrderRequest;
import com.mes.backend.dto.request.UpdateWorkOrderStatusRequest;
import com.mes.backend.dto.response.WorkOrderResponse;
import com.mes.backend.entity.WorkOrderStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * 작업지시 서비스 인터페이스
 */
public interface WorkOrderService {

    /**
     * 작업지시 생성
     */
    WorkOrderResponse createWorkOrder(CreateWorkOrderRequest request);

    /**
     * 작업지시 ID로 조회
     */
    WorkOrderResponse getWorkOrderById(Long id);

    /**
     * 지시번호로 조회
     */
    WorkOrderResponse getWorkOrderByOrderNo(String orderNo);

    /**
     * 전체 작업지시 목록 조회
     */
    List<WorkOrderResponse> getAllWorkOrders();

    /**
     * 상태별 작업지시 조회
     */
    List<WorkOrderResponse> getWorkOrdersByStatus(WorkOrderStatus status);

    /**
     * 작업지시 상태 변경
     */
    WorkOrderResponse updateWorkOrderStatus(Long id, UpdateWorkOrderStatusRequest request);
}
