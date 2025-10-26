package com.mes.backend.dto.request;

import com.mes.backend.entity.WorkOrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작업지시 상태 변경 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkOrderStatusRequest {

    /**
     * 변경할 상태
     */
    @NotNull(message = "상태는 필수입니다")
    private WorkOrderStatus status;
}
