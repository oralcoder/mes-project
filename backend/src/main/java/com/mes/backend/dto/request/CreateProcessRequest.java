package com.mes.backend.dto.request;

import com.mes.backend.entity.Process;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProcessRequest {

    @NotBlank(message = "공정명은 필수입니다")
    @Size(min = 1, max = 100, message = "공정명은 1~100자이어야 합니다")
    private String name;

    @NotNull(message = "순서는 필수입니다")
    @Min(value = 1, message = "순서는 1 이상이어야 합니다")
    private Integer sequence;

    @Size(max = 1000, message = "설명은 최대 1000자입니다")
    private String description;

    public Process toEntity() {
        return Process.builder()
                .name(this.name)
                .sequence(this.sequence)
                .description(this.description)
                .build();
    }
}
