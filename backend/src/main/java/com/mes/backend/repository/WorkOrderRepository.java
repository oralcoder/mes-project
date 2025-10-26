package com.mes.backend.repository;

import com.mes.backend.entity.WorkOrder;
import com.mes.backend.entity.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 작업지시 Repository
 */
@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    /**
     * 지시번호로 조회
     */
    Optional<WorkOrder> findByOrderNo(String orderNo);

    /**
     * 지시번호 존재 여부 확인
     */
    boolean existsByOrderNo(String orderNo);

    /**
     * 상태별 작업지시 조회
     */
    List<WorkOrder> findByStatus(WorkOrderStatus status);

    /**
     * 지시일자 범위로 조회
     */
    List<WorkOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 제품 정보를 포함하여 전체 조회 (Fetch Join)
     */
    @Query("SELECT w FROM WorkOrder w JOIN FETCH w.product")
    List<WorkOrder> findAllWithProduct();

    /**
     * 특정 작업지시 조회 시 제품 정보 함께 로드 (Fetch Join)
     */
    @Query("SELECT w FROM WorkOrder w JOIN FETCH w.product WHERE w.id = :id")
    Optional<WorkOrder> findByIdWithProduct(@Param("id") Long id);

    /**
     * 지시번호로 조회 시 제품 정보 함께 로드 (Fetch Join)
     */
    @Query("SELECT w FROM WorkOrder w JOIN FETCH w.product WHERE w.orderNo = :orderNo")
    Optional<WorkOrder> findByOrderNoWithProduct(@Param("orderNo") String orderNo);
}
