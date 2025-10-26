package com.mes.backend.dto.response;

import com.mes.backend.entity.Process;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessResponse {

    private Long id;
    private String name;
    private Integer sequence;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProcessResponse from(Process process) {
        return ProcessResponse.builder()
                .id(process.getId())
                .name(process.getName())
                .sequence(process.getSequence())
                .description(process.getDescription())
                .createdAt(process.getCreatedAt())
                .updatedAt(process.getUpdatedAt())
                .build();
    }
}
