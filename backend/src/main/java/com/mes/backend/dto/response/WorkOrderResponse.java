package com.mes.backend.dto.response;

import com.mes.backend.entity.WorkOrder;
import com.mes.backend.entity.WorkOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 작업지시 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderResponse {

    private Long id;
    private String orderNo;

    // 제품 정보
    private Long productId;
    private String productName;
    private String productCode;

    private Integer quantity;
    private WorkOrderStatus status;
    private String statusDescription;
    private LocalDateTime orderDate;
    private LocalDateTime dueDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환
     */
    public static WorkOrderResponse from(WorkOrder workOrder) {
        return WorkOrderResponse.builder()
                .id(workOrder.getId())
                .orderNo(workOrder.getOrderNo())
                .productId(workOrder.getProduct().getId())
                .productName(workOrder.getProduct().getName())
                .productCode(workOrder.getProduct().getCode())
                .quantity(workOrder.getQuantity())
                .status(workOrder.getStatus())
                .statusDescription(workOrder.getStatus().getDescription())
                .orderDate(workOrder.getOrderDate())
                .dueDate(workOrder.getDueDate())
                .startDate(workOrder.getStartDate())
                .endDate(workOrder.getEndDate())
                .createdAt(workOrder.getCreatedAt())
                .updatedAt(workOrder.getUpdatedAt())
                .build();
    }
}
