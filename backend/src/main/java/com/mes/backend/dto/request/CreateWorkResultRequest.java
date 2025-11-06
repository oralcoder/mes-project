package com.mes.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 작업실적 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWorkResultRequest {
    private Long workOrderId;
    private Long processId;
    private Long stationId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer goodQuantity;
    private Integer defectQuantity;
    private String notes;
}