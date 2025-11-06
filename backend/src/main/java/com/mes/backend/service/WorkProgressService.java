package com.mes.backend.service;

import com.mes.backend.dto.request.AdvanceProgressRequest;
import com.mes.backend.dto.response.WorkResultResponse;
import com.mes.backend.entity.WorkOrder;
import com.mes.backend.entity.WorkOrderStatus;
import com.mes.backend.entity.WorkResult;
import com.mes.backend.repository.WorkOrderRepository;
import com.mes.backend.repository.WorkResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공정 진행 서비스 (FastAPI advance_progress 로직 구현)
 */
@Service
@RequiredArgsConstructor
public class WorkProgressService {
    
    private final WorkResultRepository workResultRepository;
    private final WorkOrderRepository workOrderRepository;
    
    /**
     * 공정 진행 (FastAPI advance_progress와 동일한 로직)
     * 
     * 공정이 진행될 때마다 호출:
     * 1. WorkResult에 실적 기록 (start_time = end_time = now)
     * 2. WorkOrder 상태 업데이트
     */
    @Transactional
    public WorkResultResponse advanceProgress(AdvanceProgressRequest request) {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 작업지시 조회
        WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
            .orElseThrow(() -> new RuntimeException("작업지시를 찾을 수 없습니다: " + request.getWorkOrderId()));
        
        // 2. 실적 한 줄 추가
        WorkResult workResult = WorkResult.builder()
            .workOrderId(request.getWorkOrderId())
            .processId(request.getProcessId())
            .stationId(request.getStationId())  // nullable
            .startTime(now)
            .endTime(now)  // 단순 로직: start = end = now
            .build();
        
        WorkResult saved = workResultRepository.save(workResult);
        
        // 3. 작업지시 상태 업데이트
        updateWorkOrderStatus(workOrder, request.getProcessId(), now);
        workOrderRepository.save(workOrder);
        
        // 4. 응답 생성
        return WorkResultResponse.builder()
            .id(saved.getId())
            .workOrderId(saved.getWorkOrderId())
            .workOrderNo(workOrder.getOrderNo())
            .processId(saved.getProcessId())
            .stationId(saved.getStationId())
            .startTime(saved.getStartTime())
            .endTime(saved.getEndTime())
            .durationMinutes(saved.getDurationMinutes())
            .createdAt(saved.getCreatedAt())
            .build();
    }
    
    /**
     * 공정 ID에 따른 작업지시 상태 업데이트
     * (실제로는 Process 순서를 DB에서 조회해야 하지만, 간단히 하드코딩)
     */
    private void updateWorkOrderStatus(WorkOrder workOrder, Long processId, LocalDateTime now) {
        // 첫 공정 시작 시 작업지시 시작시간 기록
        if (workOrder.getStartDate() == null) {
            workOrder.setStartDate(now);
            workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        }
        
        // 공정 ID에 따라 상태 변경
        // 실제로는 Process의 sequence를 조회해서 판단해야 함
        // 여기서는 예시로 간단히 구현
        
        // 마지막 공정(예: processId = 4) 완료 시
        // 실제로는 모든 공정이 완료되었는지 체크해야 함
        if (isLastProcess(processId)) {
            workOrder.setStatus(WorkOrderStatus.COMPLETED);
            workOrder.setEndDate(now);
        }
    }
    
    /**
     * 마지막 공정인지 확인
     * (실제로는 DB에서 Process 순서 조회 필요)
     */
    private boolean isLastProcess(Long processId) {
        // TODO: Process 테이블에서 최대 sequence 조회
        // 임시로 processId == 4를 마지막으로 가정
        return processId.equals(4L);
    }
    
    /**
     * 작업지시의 전체 실적 조회
     */
    @Transactional(readOnly = true)
    public List<WorkResultResponse> getWorkOrderResults(Long workOrderId) {
        List<WorkResult> results = workResultRepository
            .findByWorkOrderIdOrderByStartTimeAsc(workOrderId);
        
        return results.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Entity → DTO 변환
     */
    private WorkResultResponse toResponse(WorkResult entity) {
        return WorkResultResponse.builder()
            .id(entity.getId())
            .workOrderId(entity.getWorkOrderId())
            .processId(entity.getProcessId())
            .stationId(entity.getStationId())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .durationMinutes(entity.getDurationMinutes())
            .goodQuantity(entity.getGoodQuantity())
            .defectQuantity(entity.getDefectQuantity())
            .totalQuantity(entity.getTotalQuantity())
            .defectRate(entity.getDefectRate())
            .notes(entity.getNotes())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}