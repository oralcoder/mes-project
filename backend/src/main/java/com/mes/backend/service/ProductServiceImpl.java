package com.mes.backend.service;

import com.mes.backend.dto.request.CreateProductRequest;
import com.mes.backend.dto.response.ProductResponse;
import com.mes.backend.entity.Product;
import com.mes.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 제품 서비스 구현 클래스
 * ProductService 인터페이스의 실제 구현을 담당합니다.
 */
@Service  // Spring의 서비스 빈으로 등록
@RequiredArgsConstructor  // Lombok: final 필드를 파라미터로 하는 생성자 자동 생성
@Slf4j  // Lombok: Logger 자동 생성 (log.info(), log.error() 등 사용 가능)
@Transactional(readOnly = true)  // 기본적으로 읽기 전용 트랜잭션 (성능 최적화)
public class ProductServiceImpl implements ProductService {

    /**
     * 제품 Repository
     * final 키워드로 불변성 보장 (생성자 주입 방식)
     */
    private final ProductRepository productRepository;

    /**
     * 제품 생성
     *
     * 비즈니스 로직:
     * 1. 제품 코드 중복 체크
     * 2. 제품 저장
     * 3. 로그 기록
     *
     * @param request 제품 생성 요청 DTO
     * @return 생성된 제품 응답 DTO
     * @throws IllegalArgumentException 제품 코드가 이미 존재하는 경우
     */
    @Override
    @Transactional  // 쓰기 작업이므로 readOnly = false (기본값)
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("제품 생성 요청: code={}, name={}", request.getCode(), request.getName());

        // 1. 제품 코드 중복 체크
        if (productRepository.existsByCode(request.getCode())) {
            log.warn("제품 코드 중복: code={}", request.getCode());
            throw new IllegalArgumentException("이미 존재하는 제품 코드입니다: " + request.getCode());
        }

        // 2. DTO를 Entity로 변환
        Product product = request.toEntity();

        // 3. DB에 저장
        Product savedProduct = productRepository.save(product);

        log.info("제품 생성 완료: id={}, code={}", savedProduct.getId(), savedProduct.getCode());
        
        // 4. Entity를 DTO로 변환하여 반환
        return ProductResponse.from(savedProduct);
    }
    
    /**
     * 제품 ID로 조회
     * 
     * @param id 제품 ID
     * @return 제품 응답 DTO
     * @throws IllegalArgumentException 제품을 찾을 수 없는 경우
     */
    @Override
    public ProductResponse getProductById(Long id) {
        log.info("제품 조회 요청: id={}", id);
        
        // Repository에서 조회
        // Optional.orElseThrow()로 값이 없으면 예외 발생
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("제품을 찾을 수 없음: id={}", id);
                    return new IllegalArgumentException("제품을 찾을 수 없습니다: " + id);
                });
        
        log.info("제품 조회 완료: id={}, code={}", product.getId(), product.getCode());
        
        // Entity를 DTO로 변환하여 반환
        return ProductResponse.from(product);
    }
    
    /**
     * 전체 제품 목록 조회
     * 
     * @return 제품 응답 DTO 리스트
     */
    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("전체 제품 목록 조회 요청");
        
        // 모든 제품 조회
        List<Product> products = productRepository.findAll();
        
        log.info("제품 목록 조회 완료: count={}", products.size());
        
        // Entity 리스트를 DTO 리스트로 변환
        // Stream API 사용
        return products.stream()
                .map(ProductResponse::from)  // 각 Product를 ProductResponse로 변환
                .collect(Collectors.toList());  // List로 수집
    }
}
