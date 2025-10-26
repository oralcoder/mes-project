package com.mes.backend.entity;

/**
 * 스테이션 상태 Enum
 */
public enum StationStatus {
    /**
     * 가동 중
     */
    RUNNING("가동"),

    /**
     * 정지 (일시 중단)
     */
    STOPPED("정지"),

    /**
     * 고장
     */
    FAULT("고장"),

    /**
     * 유지보수 중
     */
    MAINTENANCE("유지보수");

    private final String description;

    StationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
