package com.mes.backend.controller;

import com.mes.backend.dto.request.CreateProductRequest;
import com.mes.backend.dto.response.ProductResponse;
import com.mes.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 제품 컨트롤러
 * 제품 관련 REST API를 제공합니다.
 *
 * Base URL: /api/products
 */
@RestController  // @Controller + @ResponseBody (JSON 자동 변환)
@RequestMapping("/api/products")  // 기본 URL 경로
@RequiredArgsConstructor  // 생성자 주입
@Slf4j  // 로깅
public class ProductController {

    /**
     * 제품 서비스
     * final 키워드로 불변성 보장
     */
    private final ProductService productService;

    /**
     * 전체 제품 목록 조회
     *
     * GET /api/products
     *
     * @return 200 OK + 제품 목록
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("GET /api/products - 전체 제품 목록 조회 요청");

        // Service 호출
        List<ProductResponse> products = productService.getAllProducts();

        log.info("전체 제품 목록 조회 성공: count={}", products.size());

        // 200 OK 응답
        return ResponseEntity.ok(products);
    }

    /**
     * 제품 단건 조회
     *
     * GET /api/products/{id}
     *
     * @param id 제품 ID (Path Variable)
     * @return 200 OK + 제품 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long id  // URL 경로의 {id}를 파라미터로 받음
    ) {
        log.info("GET /api/products/{} - 제품 조회 요청", id);

        // Service 호출
        ProductResponse product = productService.getProductById(id);

        log.info("제품 조회 성공: id={}, code={}", product.getId(), product.getCode());

        // 200 OK 응답
        return ResponseEntity.ok(product);
    }

    /**
     * 제품 생성
     *
     * POST /api/products
     *
     * @param request 제품 생성 요청 DTO (Request Body)
     * @return 201 Created + 생성된 제품 정보
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request
            // @Valid: DTO의 검증 어노테이션 실행
            // @RequestBody: HTTP Body의 JSON을 DTO로 변환
    ) {
        log.info("POST /api/products - 제품 생성 요청: code={}, name={}",
                request.getCode(), request.getName());

        // Service 호출
        ProductResponse product = productService.createProduct(request);

        log.info("제품 생성 성공: id={}, code={}", product.getId(), product.getCode());

        // 201 Created 응답
        // status(HttpStatus.CREATED): 201 상태 코드
        // body(product): 응답 본문
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);
    }
}
