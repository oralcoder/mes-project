package com.mes.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 표준시간 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandardTimeResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long processId;
    private String processName;
    private Integer processSequence;
    private Integer standardMinutes;
    private Integer standardQuantity;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}