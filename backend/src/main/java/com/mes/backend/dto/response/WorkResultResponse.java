package com.mes.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 작업실적 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkResultResponse {
    private Long id;
    private Long workOrderId;
    private String workOrderNo;
    private Long processId;
    private String processName;
    private Long stationId;
    private String stationName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;
    private Integer goodQuantity;
    private Integer defectQuantity;
    private Integer totalQuantity;
    private Double defectRate;
    private String notes;
    private LocalDateTime createdAt;
}