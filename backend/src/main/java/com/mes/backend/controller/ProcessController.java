package com.mes.backend.controller;

import com.mes.backend.dto.request.CreateProcessRequest;
import com.mes.backend.dto.response.ProcessResponse;
import com.mes.backend.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;

/**
 * 공정 컨트롤러
 * Base URL: /api/processes
 */
@RestController
@RequestMapping("/api/processes")
@RequiredArgsConstructor
@Slf4j
public class ProcessController {

    private final ProcessService processService;

    /**
     * 전체 공정 목록 조회
     * GET /api/processes
     */
    @GetMapping
    public ResponseEntity<List<ProcessResponse>> getAllProcesses() {
        log.info("GET /api/processes - 전체 공정 목록 조회 요청");
        List<ProcessResponse> processes = processService.getAllProcesses();
        log.info("전체 공정 목록 조회 성공: count={}", processes.size());
        return ResponseEntity.ok(processes);
    }

    /**
     * 공정 단건 조회
     * GET /api/processes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProcessResponse> getProcess(@PathVariable Long id) {
        log.info("GET /api/processes/{} - 공정 조회 요청", id);
        ProcessResponse process = processService.getProcessById(id);
        log.info("공정 조회 성공: id={}, name={}", process.getId(), process.getName());
        return ResponseEntity.ok(process);
    }

    /**
     * 공정 생성
     * POST /api/processes
     */
    @PostMapping
    public ResponseEntity<ProcessResponse> createProcess(
            @Valid @RequestBody CreateProcessRequest request
    ) {
        log.info("POST /api/processes - 공정 생성 요청: name={}", request.getName());
        ProcessResponse process = processService.createProcess(request);
        log.info("공정 생성 성공: id={}, name={}", process.getId(), process.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(process);
    }
}
