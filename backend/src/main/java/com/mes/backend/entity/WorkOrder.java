package com.mes.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 작업지시 엔티티
 */
@Entity
@Table(name = "work_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 지시번호
     * 예: WO-2025-001
     */
    @Column(name = "order_no", nullable = false, length = 50, unique = true)
    private String orderNo;

    /**
     * 제품 정보
     * N:1 관계 (여러 작업지시가 하나의 제품 참조)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 생산 수량
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * 작업지시 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WorkOrderStatus status;

    /**
     * 지시일시
     */
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    /**
     * 납기일
     */
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    /**
     * 작업 시작일시
     * 상태가 IN_PROGRESS로 전환될 때 자동 설정
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * 작업 완료일시
     * 상태가 COMPLETED로 전환될 때 자동 설정
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 작업 시작 (상태를 IN_PROGRESS로 전환)
     */
    public void start() {
        if (!this.status.canTransitionTo(WorkOrderStatus.IN_PROGRESS)) {
            throw new IllegalStateException(
                String.format("작업지시를 시작할 수 없습니다. 현재 상태: %s", this.status)
            );
        }
        this.status = WorkOrderStatus.IN_PROGRESS;
        this.startDate = LocalDateTime.now();
    }

    /**
     * 작업 완료 (상태를 COMPLETED로 전환)
     */
    public void complete() {
        if (!this.status.canTransitionTo(WorkOrderStatus.COMPLETED)) {
            throw new IllegalStateException(
                String.format("작업지시를 완료할 수 없습니다. 현재 상태: %s", this.status)
            );
        }
        this.status = WorkOrderStatus.COMPLETED;
        this.endDate = LocalDateTime.now();
    }

    /**
     * 작업 취소 (상태를 CANCELLED로 전환)
     */
    public void cancel() {
        if (!this.status.canTransitionTo(WorkOrderStatus.CANCELLED)) {
            throw new IllegalStateException(
                String.format("작업지시를 취소할 수 없습니다. 현재 상태: %s", this.status)
            );
        }
        this.status = WorkOrderStatus.CANCELLED;
    }
}
