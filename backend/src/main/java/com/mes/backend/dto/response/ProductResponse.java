package com.mes.backend.dto.response;

import com.mes.backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 제품 응답 DTO
 * API 응답 시 사용됩니다.
 * Entity의 모든 정보를 클라이언트에게 전달합니다.
 */
@Getter  // getter 메서드 자동 생성
@NoArgsConstructor  // 기본 생성자
@AllArgsConstructor  // 모든 필드 생성자
@Builder  // 빌더 패턴
public class ProductResponse {

    /**
     * 제품 ID
     */
    private Long id;

    /**
     * 제품명
     */
    private String name;

    /**
     * 제품 코드
     */
    private String code;

    /**
     * 규격
     */
    private String spec;

    /**
     * 단위
     */
    private String unit;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 정적 메서드
     *
     * 사용 예:
     * Product product = ...;
     * ProductResponse response = ProductResponse.from(product);
     *
     * @param product 제품 Entity
     * @return ProductResponse DTO
     */
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .code(product.getCode())
                .spec(product.getSpec())
                .unit(product.getUnit())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
