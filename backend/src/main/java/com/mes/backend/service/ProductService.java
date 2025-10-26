package com.mes.backend.service;

import com.mes.backend.dto.request.CreateProductRequest;
import com.mes.backend.dto.response.ProductResponse;

import java.util.List;

/**
 * 제품 서비스 인터페이스
 * 제품 관련 비즈니스 로직을 정의합니다.
 */
public interface ProductService {

    /**
     * 제품 생성
     *
     * @param request 제품 생성 요청 DTO
     * @return 생성된 제품 응답 DTO
     */
    ProductResponse createProduct(CreateProductRequest request);

    /**
     * 제품 ID로 조회
     *
     * @param id 제품 ID
     * @return 제품 응답 DTO
     */
    ProductResponse getProductById(Long id);

    /**
     * 전체 제품 목록 조회
     *
     * @return 제품 응답 DTO 리스트
     */
    List<ProductResponse> getAllProducts();
}
