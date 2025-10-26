package com.mes.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 공정 엔티티
 * 제조 공정 정보를 관리합니다.
 */
@Entity
@Table(name = "process")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 이 공정에 속한 스테이션 목록
     * mappedBy: Station 엔티티의 'process' 필드가 연관관계의 주인
     */
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Station> stations = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
