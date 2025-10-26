package com.mes.backend.repository;

import com.mes.backend.entity.Station;
import com.mes.backend.entity.StationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 스테이션 Repository
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    /**
     * 스테이션 코드로 조회
     */
    Optional<Station> findByCode(String code);

    /**
     * 스테이션 코드 존재 여부 확인
     */
    boolean existsByCode(String code);

    /**
     * 공정 ID로 스테이션 목록 조회
     */
    List<Station> findByProcessId(Long processId);

    /**
     * 상태별 스테이션 조회
     */
    List<Station> findByStatus(StationStatus status);

    /**
     * 공정 정보를 포함하여 전체 조회 (Fetch Join)
     * N+1 문제 해결
     */
    @Query("SELECT s FROM Station s JOIN FETCH s.process")
    List<Station> findAllWithProcess();

    /**
     * 특정 스테이션 조회 시 공정 정보 함께 로드 (Fetch Join)
     */
    @Query("SELECT s FROM Station s JOIN FETCH s.process WHERE s.id = :id")
    Optional<Station> findByIdWithProcess(@Param("id") Long id);
}
