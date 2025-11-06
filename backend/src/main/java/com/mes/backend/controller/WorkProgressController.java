package com.mes.backend.controller;

import com.mes.backend.dto.request.AdvanceProgressRequest;
import com.mes.backend.dto.response.WorkResultResponse;
import com.mes.backend.service.WorkProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공정 진행 API
 */
@RestController
@RequestMapping("/api/work-progress")
@RequiredArgsConstructor
public class WorkProgressController {
    
    private final WorkProgressService workProgressService;
    
    /**
     * 공정 진행 (FastAPI advance_progress와 동일)
     * 
     * POST /api/work-progress/advance
     * {
     *   "workOrderId": 1,
     *   "processId": 2,
     *   "stationId": 5
     * }
     */
    @PostMapping("/advance")
    public ResponseEntity<WorkResultResponse> advanceProgress(
            @RequestBody AdvanceProgressRequest request) {
        WorkResultResponse response = workProgressService.advanceProgress(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * 작업지시의 전체 실적 조회
     * 
     * GET /api/work-progress/work-order/{workOrderId}
     */
    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<List<WorkResultResponse>> getWorkOrderResults(
            @PathVariable Long workOrderId) {
        List<WorkResultResponse> results = workProgressService.getWorkOrderResults(workOrderId);
        return ResponseEntity.ok(results);
    }
}