package com.mes.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 표준시간 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStandardTimeRequest {
    private Long productId;
    private Long processId;
    private Integer standardMinutes;
    private Integer standardQuantity;
    private String notes;
}