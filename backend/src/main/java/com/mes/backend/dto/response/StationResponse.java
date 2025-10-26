package com.mes.backend.dto.response;

import com.mes.backend.entity.Station;
import com.mes.backend.entity.StationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 스테이션 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationResponse {

    private Long id;
    private String name;
    private String code;
    private Long processId;
    private String processName;
    private StationStatus status;
    private String statusDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환
     */
    public static StationResponse from(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .code(station.getCode())
                .processId(station.getProcess().getId())
                .processName(station.getProcess().getName())
                .status(station.getStatus())
                .statusDescription(station.getStatus().getDescription())
                .createdAt(station.getCreatedAt())
                .updatedAt(station.getUpdatedAt())
                .build();
    }
}
