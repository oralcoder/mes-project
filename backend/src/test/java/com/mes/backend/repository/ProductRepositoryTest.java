package com.mes.backend.repository;

import com.mes.backend.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Product Repository 테스트")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        // Given: Builder 패턴으로 테스트용 제품 데이터 생성
        testProduct = Product.builder()
            .name("온도센서 모듈 TS-100")
            .code("TS100")
            .spec("10mm x 10mm")
            .unit("EA")
            .build();
    }
    
    @Test
    @DisplayName("제품 저장 테스트")
    void testSaveProduct() {
        // When: 제품 저장
        Product savedProduct = productRepository.save(testProduct);
        
        // Then: 저장 결과 검증
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
        assertEquals(testProduct.getCode(), savedProduct.getCode());
    }
    
    @Test
    @DisplayName("ID로 제품 조회 테스트")
    void testFindById() {
        // Given: 제품 저장
        Product savedProduct = productRepository.save(testProduct);
        Long productId = savedProduct.getId();
        
        // When: ID로 조회
        Optional<Product> foundProduct = productRepository.findById(productId);
        
        // Then: 조회 결과 검증
        assertTrue(foundProduct.isPresent());
        assertEquals(productId, foundProduct.get().getId());
        assertEquals("온도센서 모듈 TS-100", foundProduct.get().getName());
    }
    
    @Test
    @DisplayName("존재하지 않는 ID로 조회 테스트")
    void testFindByIdNotFound() {
        // Given: 존재하지 않는 ID
        Long nonExistentId = 999L;
        
        // When: 조회
        Optional<Product> foundProduct = productRepository.findById(nonExistentId);
        
        // Then: 결과가 없어야 함
        assertFalse(foundProduct.isPresent());
        assertTrue(foundProduct.isEmpty());
    }
    
    @Test
    @DisplayName("전체 제품 조회 테스트")
    void testFindAll() {
        // Given: Builder로 여러 제품 생성 및 저장
        Product product1 = Product.builder()
            .name("제품1")
            .code("P001")
            .spec("10x10")
            .unit("EA")
            .build();
        
        Product product2 = Product.builder()
            .name("제품2")
            .code("P002")
            .spec("20x20")
            .unit("EA")
            .build();
        
        Product product3 = Product.builder()
            .name("제품3")
            .code("P003")
            .spec("30x30")
            .unit("EA")
            .build();
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
        
        // When: 전체 조회
        List<Product> products = productRepository.findAll();
        
        // Then: 개수 검증
        assertEquals(3, products.size());
    }
    
    @Test
    @DisplayName("제품 수정 테스트")
    void testUpdateProduct() {
        // Given: 제품 저장
        Product savedProduct = productRepository.save(testProduct);
        Long productId = savedProduct.getId();
        
        // When: 제품 정보 수정
        savedProduct.setName("수정된 제품명");
        savedProduct.setSpec("20mm x 20mm");
        productRepository.save(savedProduct);
        
        // Then: 수정 결과 검증
        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertEquals("수정된 제품명", updatedProduct.getName());
        assertEquals("20mm x 20mm", updatedProduct.getSpec());
        assertEquals("TS100", updatedProduct.getCode());
    }
    
    @Test
    @DisplayName("제품 삭제 테스트")
    void testDeleteProduct() {
        // Given: 제품 저장
        Product savedProduct = productRepository.save(testProduct);
        Long productId = savedProduct.getId();
        
        // When: 제품 삭제
        productRepository.deleteById(productId);
        
        // Then: 삭제 확인
        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }
    
    @Test
    @DisplayName("제품 개수 확인 테스트")
    void testCountProducts() {
        // Given: Builder로 여러 제품 생성 및 저장
        Product product1 = Product.builder()
            .name("제품1")
            .code("P001")
            .spec("10x10")
            .unit("EA")
            .build();
        
        Product product2 = Product.builder()
            .name("제품2")
            .code("P002")
            .spec("20x20")
            .unit("EA")
            .build();
        
        productRepository.save(product1);
        productRepository.save(product2);
        
        // When: 개수 조회
        long count = productRepository.count();
        
        // Then: 개수 검증
        assertEquals(2, count);
    }
}