package com.mes.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 에러 응답 DTO
 * 일관된 에러 응답 형식을 제공합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * 에러 코드
     */
    private String errorCode;

    /**
     * 에러 메시지
     */
    private String message;

    /**
     * 상세 정보
     */
    private String detail;

    /**
     * 발생 시각
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 요청 경로
     */
    private String path;
}
