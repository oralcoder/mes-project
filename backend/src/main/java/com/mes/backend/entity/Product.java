package com.mes.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 제품 엔티티
 * 데이터베이스의 product 테이블과 매핑됩니다.
 */
@Entity  // JPA 엔티티임을 선언
@Table(name = "product")  // 테이블 이름 지정 (생략 시 클래스명 소문자)
@Getter  // Lombok: 모든 필드의 getter 메서드 자동 생성
@Setter  // Lombok: 모든 필드의 setter 메서드 자동 생성
@NoArgsConstructor  // Lombok: 파라미터 없는 기본 생성자 생성 (JPA 필수)
@AllArgsConstructor  // Lombok: 모든 필드를 파라미터로 받는 생성자 생성
@Builder  // Lombok: 빌더 패턴 사용 가능
public class Product {

    /**
     * 제품 ID (Primary Key)
     * 데이터베이스에서 자동으로 증가하는 값
     */
    @Id  // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT 전략
    private Long id;

    /**
     * 제품명
     * 필수 입력 항목
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 제품 코드
     * 유일한 값이어야 함 (중복 불가)
     */
    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    /**
     * 규격
     * 선택 입력 항목 (예: "10mm x 10mm")
     */
    @Column(name = "spec", length = 200)
    private String spec;

    /**
     * 단위
     * 필수 입력 항목 (예: "EA", "BOX", "KG")
     */
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    /**
     * 생성일시
     * INSERT 시 자동으로 현재 시간 저장
     */
    @CreationTimestamp  // INSERT 시 자동으로 현재 시간 설정
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     * UPDATE 시 자동으로 현재 시간 갱신
     */
    @UpdateTimestamp  // UPDATE 시 자동으로 현재 시간 설정
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
