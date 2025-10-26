package com.mes.backend.repository;

import com.mes.backend.entity.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 공정 Repository
 */
@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {

    /**
     * 공정명으로 조회
     */
    Optional<Process> findByName(String name);

    /**
     * 공정명 존재 여부 확인
     */
    boolean existsByName(String name);

    /**
     * 순서로 정렬하여 전체 조회
     */
    List<Process> findAllByOrderBySequenceAsc();
}
