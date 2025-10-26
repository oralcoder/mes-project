package com.mes.backend.dto.request;

import com.mes.backend.entity.StationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 스테이션 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStationRequest {

    @NotBlank(message = "스테이션명은 필수입니다")
    @Size(min = 1, max = 100, message = "스테이션명은 1~100자이어야 합니다")
    private String name;

    @NotBlank(message = "스테이션 코드는 필수입니다")
    @Pattern(regexp = "^STATION-[A-Z0-9]{1,20}$",
             message = "스테이션 코드는 'STATION-'으로 시작하고 영문 대문자와 숫자만 가능합니다")
    private String code;

    @NotNull(message = "공정 ID는 필수입니다")
    private Long processId;

    @NotNull(message = "상태는 필수입니다")
    private StationStatus status;
}
