package com.mes.backend.service;

import com.mes.backend.dto.request.CreateStationRequest;
import com.mes.backend.dto.response.StationResponse;

import java.util.List;

/**
 * 스테이션 서비스 인터페이스
 */
public interface StationService {

    /**
     * 스테이션 생성
     */
    StationResponse createStation(CreateStationRequest request);

    /**
     * 스테이션 ID로 조회
     */
    StationResponse getStationById(Long id);

    /**
     * 전체 스테이션 목록 조회
     */
    List<StationResponse> getAllStations();

    /**
     * 공정별 스테이션 목록 조회
     */
    List<StationResponse> getStationsByProcessId(Long processId);
}
