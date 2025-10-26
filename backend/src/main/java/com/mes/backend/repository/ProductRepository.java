package com.mes.backend.repository;

import com.mes.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 제품 Repository
 * JpaRepository를 상속받아 기본 CRUD 메서드를 자동으로 사용
 *
 * JpaRepository<Entity타입, ID타입>
 * - Entity타입: Product
 * - ID타입: Long
 */
@Repository  // Repository 빈으로 등록
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 제품 코드로 제품 조회
     *
     * 메서드 이름 규칙: findBy + 필드명
     * - findByCode → WHERE code = ?
     * - findByName → WHERE name = ?
     * - findByNameAndCode → WHERE name = ? AND code = ?
     *
     * @param code 제품 코드
     * @return Optional<Product> (값이 있을 수도, 없을 수도 있음)
     */
    Optional<Product> findByCode(String code);

    /**
     * 제품명으로 제품 존재 여부 확인
     *
     * 메서드 이름 규칙: existsBy + 필드명
     *
     * @param name 제품명
     * @return boolean (존재하면 true, 없으면 false)
     */
    boolean existsByName(String name);

    /**
     * 제품 코드로 제품 존재 여부 확인
     *
     * @param code 제품 코드
     * @return boolean (존재하면 true, 없으면 false)
     */
    boolean existsByCode(String code);
}
