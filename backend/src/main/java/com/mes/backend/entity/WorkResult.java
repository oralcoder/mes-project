package com.mes.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 작업실적 (트랜잭션 데이터)
 * 공정 진행 시마다 기록
 */
@Entity
@Table(name = "work_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 작업지시 참조 (FK만 저장)
    @Column(name = "work_order_id", nullable = false)
    private Long workOrderId;
    
    // 공정 참조 (FK만 저장)
    @Column(name = "process_id", nullable = false)
    private Long processId;
    
    // 설비 참조 (FK만 저장, nullable)
    @Column(name = "station_id")
    private Long stationId;
    
    // 작업 시작 시간
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    // 작업 종료 시간
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    // 생산 수량 (선택사항)
    @Column(name = "good_quantity")
    private Integer goodQuantity;
    
    // 불량 수량 (선택사항)
    @Column(name = "defect_quantity")
    private Integer defectQuantity;
    
    // 비고
    @Column(length = 500)
    private String notes;
    
    // 생성 시간
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // 편의 메서드: 작업 소요 시간 계산 (분)
    public Long getDurationMinutes() {
        if (startTime != null && endTime != null) {
            return ChronoUnit.MINUTES.between(startTime, endTime);
        }
        return null;
    }
    
    // 편의 메서드: 작업 소요 시간 계산 (초)
    public Long getDurationSeconds() {
        if (startTime != null && endTime != null) {
            return ChronoUnit.SECONDS.between(startTime, endTime);
        }
        return null;
    }
    
    // 편의 메서드: 총 생산 수량
    public Integer getTotalQuantity() {
        int good = (goodQuantity != null) ? goodQuantity : 0;
        int defect = (defectQuantity != null) ? defectQuantity : 0;
        return good + defect;
    }
    
    // 편의 메서드: 불량률 계산 (%)
    public Double getDefectRate() {
        int total = getTotalQuantity();
        if (total > 0 && defectQuantity != null) {
            return (defectQuantity * 100.0) / total;
        }
        return 0.0;
    }
    
    // 편의 메서드: 시간당 생산량
    public Double getProductionRatePerHour() {
        Long minutes = getDurationMinutes();
        if (minutes != null && minutes > 0) {
            int total = getTotalQuantity();
            return (total * 60.0) / minutes;
        }
        return null;
    }
}