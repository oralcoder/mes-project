package com.mes.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공정별 실적 통계 응답 DTO (데이터 분석용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessStatistics {
    private Long processId;
    private String processName;
    private Integer standardMinutes;
    private Double averageActualMinutes;
    private Double efficiency;  // (표준시간 / 실제시간) * 100
    private Integer totalProduction;
    private Integer totalDefects;
    private Double defectRate;
}