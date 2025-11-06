package com.mes.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 표준시간 마스터
 * 제품별 × 공정별 표준 작업시간을 정의
 */
@Entity
@Table(
    name = "standard_time",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "process_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandardTime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 제품 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    // 공정 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private Process process;
    
    // 표준 작업시간 (분)
    @Column(name = "standard_minutes", nullable = false)
    private Integer standardMinutes;
    
    // 표준 생산량 (해당 시간 동안 생산 가능한 수량)
    @Column(name = "standard_quantity")
    private Integer standardQuantity;
    
    // 비고
    @Column(length = 500)
    private String notes;
    
    // 생성/수정 시간
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 편의 메서드: 시간당 생산량 계산
    public Double getProductionRatePerHour() {
        if (standardMinutes != null && standardMinutes > 0 && standardQuantity != null) {
            return (standardQuantity * 60.0) / standardMinutes;
        }
        return null;
    }
    
    // 편의 메서드: 단위 수량당 소요 시간 (초)
    public Double getSecondsPerUnit() {
        if (standardQuantity != null && standardQuantity > 0 && standardMinutes != null) {
            return (standardMinutes * 60.0) / standardQuantity;
        }
        return null;
    }
}