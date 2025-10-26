package com.mes.backend.dto.request;

import com.mes.backend.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 제품 생성 요청 DTO
 * 클라이언트가 제품 생성 시 전송하는 데이터입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    /**
     * 제품명
     * 필수 입력, 1~100자
     */
    @NotBlank(message = "제품명은 필수입니다")
    @Size(min = 1, max = 100, message = "제품명은 1~100자이어야 합니다")
    private String name;

    /**
     * 제품 코드
     * 필수 입력, 영문 대문자와 숫자만 가능
     */
    @NotBlank(message = "제품 코드는 필수입니다")
    @Pattern(regexp = "^[A-Z0-9]{2,50}$",
             message = "제품 코드는 영문 대문자와 숫자 2~50자이어야 합니다")
    private String code;

    /**
     * 규격
     * 선택 입력, 최대 200자
     */
    @Size(max = 200, message = "규격은 최대 200자입니다")
    private String spec;

    /**
     * 단위
     * 필수 입력, 1~20자
     */
    @NotBlank(message = "단위는 필수입니다")
    @Size(min = 1, max = 20, message = "단위는 1~20자이어야 합니다")
    private String unit;

    /**
     * DTO를 Entity로 변환하는 메서드
     *
     * 사용 예:
     * CreateProductRequest request = ...;
     * Product product = request.toEntity();
     *
     * @return Product Entity
     */
    public Product toEntity() {
        return Product.builder()
                .name(this.name)
                .code(this.code)
                .spec(this.spec)
                .unit(this.unit)
                .build();
    }
}
