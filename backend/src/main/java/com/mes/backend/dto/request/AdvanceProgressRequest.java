package com.mes.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공정 진행 요청 DTO (FastAPI advance_progress 스타일)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvanceProgressRequest {
    private Long workOrderId;
    private Long processId;
    private Long stationId;  // nullable
}