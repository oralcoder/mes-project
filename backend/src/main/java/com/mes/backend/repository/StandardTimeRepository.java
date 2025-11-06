package com.mes.backend.repository;

import com.mes.backend.entity.StandardTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StandardTimeRepository extends JpaRepository<StandardTime, Long> {
    
    /**
     * 제품 ID로 표준시간 조회
     */
    List<StandardTime> findByProductId(Long productId);
    
    /**
     * 공정 ID로 표준시간 조회
     */
    List<StandardTime> findByProcessId(Long processId);
    
    /**
     * 제품 ID와 공정 ID로 표준시간 조회 (특정 조합)
     */
    Optional<StandardTime> findByProductIdAndProcessId(Long productId, Long processId);
    
    /**
     * 제품별 전체 표준시간 합계 (분)
     */
    @Query("SELECT SUM(st.standardMinutes) FROM StandardTime st WHERE st.product.id = :productId")
    Integer sumStandardMinutesByProductId(@Param("productId") Long productId);
    
    /**
     * 제품별 공정 순서에 따라 정렬된 표준시간 조회
     */
    @Query("SELECT st FROM StandardTime st " +
           "JOIN st.process p " +
           "WHERE st.product.id = :productId " +
           "ORDER BY p.sequence ASC")
    List<StandardTime> findByProductIdOrderByProcessSequence(@Param("productId") Long productId);
}