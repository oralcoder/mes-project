package com.mes.backend.repository;

import com.mes.backend.entity.WorkResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkResultRepository extends JpaRepository<WorkResult, Long> {
    
    /**
     * 작업지시 ID로 실적 조회
     */
    List<WorkResult> findByWorkOrderId(Long workOrderId);
    
    /**
     * 작업지시 ID로 실적 조회 (시작시간 순 정렬)
     */
    List<WorkResult> findByWorkOrderIdOrderByStartTimeAsc(Long workOrderId);
    
    /**
     * 공정 ID로 실적 조회
     */
    List<WorkResult> findByProcessId(Long processId);
    
    /**
     * 설비 ID로 실적 조회
     */
    List<WorkResult> findByStationId(Long stationId);
    
    /**
     * 기간별 실적 조회
     */
    List<WorkResult> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 작업지시 ID와 공정 ID로 실적 조회
     */
    List<WorkResult> findByWorkOrderIdAndProcessId(Long workOrderId, Long processId);
    
    /**
     * 공정별 평균 작업 시간 (분)
     */
    @Query("SELECT wr.processId, " +
           "AVG(FUNCTION('TIMESTAMPDIFF', MINUTE, wr.startTime, wr.endTime)) " +
           "FROM WorkResult wr " +
           "GROUP BY wr.processId")
    List<Object[]> findAverageDurationByProcess();
    
    /**
     * 설비별 총 작업 횟수
     */
    @Query("SELECT wr.stationId, COUNT(wr) " +
           "FROM WorkResult wr " +
           "WHERE wr.stationId IS NOT NULL " +
           "GROUP BY wr.stationId")
    List<Object[]> countByStation();
    
    /**
     * 특정 기간 동안의 총 생산량
     */
    @Query("SELECT SUM(wr.goodQuantity) " +
           "FROM WorkResult wr " +
           "WHERE wr.startTime BETWEEN :startDate AND :endDate")
    Integer sumGoodQuantityBetween(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 기간 동안의 총 불량량
     */
    @Query("SELECT SUM(wr.defectQuantity) " +
           "FROM WorkResult wr " +
           "WHERE wr.startTime BETWEEN :startDate AND :endDate")
    Integer sumDefectQuantityBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * 최근 N개 실적 조회
     */
    List<WorkResult> findTop10ByOrderByCreatedAtDesc();
}