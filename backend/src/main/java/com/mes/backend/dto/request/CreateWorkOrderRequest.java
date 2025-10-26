package com.mes.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 작업지시 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkOrderRequest {

    /**
     * 지시번호
     */
    @NotBlank(message = "지시번호는 필수입니다")
    @Pattern(regexp = "^WO-\\d{4}-\\d{3,}$",
             message = "지시번호 형식이 올바르지 않습니다 (예: WO-2025-001)")
    private String orderNo;

    /**
     * 제품 ID
     */
    @NotNull(message = "제품 ID는 필수입니다")
    private Long productId;

    /**
     * 수량
     */
    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
    private Integer quantity;

    /**
     * 지시일자
     */
    @NotNull(message = "지시일자는 필수입니다")
    private LocalDate orderDate;
}
