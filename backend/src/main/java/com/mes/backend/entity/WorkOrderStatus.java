package com.mes.backend.entity;

/**
 * 작업지시 상태 Enum
 */
public enum WorkOrderStatus {
    /**
     * 계획 (생성 직후 상태)
     */
    PLANNED("계획"),

    /**
     * 진행 중 (작업 시작됨)
     */
    IN_PROGRESS("진행중"),

    /**
     * 완료 (작업 종료됨)
     */
    COMPLETED("완료"),

    /**
     * 취소
     */
    CANCELLED("취소");

    private final String description;

    WorkOrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 상태 전환 가능 여부 확인
     */
    public boolean canTransitionTo(WorkOrderStatus newStatus) {
        switch (this) {
            case PLANNED:
                // 계획 상태에서는 진행중 또는 취소로만 전환 가능
                return newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS:
                // 진행중 상태에서는 완료로만 전환 가능
                return newStatus == COMPLETED;
            case COMPLETED:
            case CANCELLED:
                // 완료 또는 취소 상태에서는 전환 불가
                return false;
            default:
                return false;
        }
    }
}
