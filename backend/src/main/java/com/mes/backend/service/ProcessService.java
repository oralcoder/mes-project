package com.mes.backend.service;

import com.mes.backend.dto.request.CreateProcessRequest;
import com.mes.backend.dto.response.ProcessResponse;

import java.util.List;

/**
 * 공정 서비스 인터페이스
 */
public interface ProcessService {

    /**
     * 공정 생성
     */
    ProcessResponse createProcess(CreateProcessRequest request);

    /**
     * 공정 ID로 조회
     */
    ProcessResponse getProcessById(Long id);

    /**
     * 전체 공정 목록 조회 (순서대로)
     */
    List<ProcessResponse> getAllProcesses();
}
