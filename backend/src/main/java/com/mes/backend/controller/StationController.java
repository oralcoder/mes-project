package com.mes.backend.controller;

import com.mes.backend.dto.request.CreateStationRequest;
import com.mes.backend.dto.response.StationResponse;
import com.mes.backend.service.StationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 스테이션 컨트롤러
 * Base URL: /api/stations
 */
@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
@Slf4j
public class StationController {

    private final StationService stationService;

    /**
     * 전체 스테이션 목록 조회
     * GET /api/stations
     */
    @GetMapping
    public ResponseEntity<List<StationResponse>> getAllStations() {
        log.info("GET /api/stations - 전체 스테이션 목록 조회 요청");
        List<StationResponse> stations = stationService.getAllStations();
        log.info("전체 스테이션 목록 조회 성공: count={}", stations.size());
        return ResponseEntity.ok(stations);
    }

    /**
     * 스테이션 단건 조회
     * GET /api/stations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> getStation(@PathVariable Long id) {
        log.info("GET /api/stations/{} - 스테이션 조회 요청", id);
        StationResponse station = stationService.getStationById(id);
        log.info("스테이션 조회 성공: id={}, code={}", station.getId(), station.getCode());
        return ResponseEntity.ok(station);
    }

    /**
     * 공정별 스테이션 목록 조회
     * GET /api/stations?processId={processId}
     */
    @GetMapping(params = "processId")
    public ResponseEntity<List<StationResponse>> getStationsByProcess(
            @RequestParam Long processId
    ) {
        log.info("GET /api/stations?processId={} - 공정별 스테이션 목록 조회 요청", processId);
        List<StationResponse> stations = stationService.getStationsByProcessId(processId);
        log.info("공정별 스테이션 목록 조회 성공: processId={}, count={}", processId, stations.size());
        return ResponseEntity.ok(stations);
    }

    /**
     * 스테이션 생성
     * POST /api/stations
     */
    @PostMapping
    public ResponseEntity<StationResponse> createStation(
            @Valid @RequestBody CreateStationRequest request
    ) {
        log.info("POST /api/stations - 스테이션 생성 요청: code={}", request.getCode());
        StationResponse station = stationService.createStation(request);
        log.info("스테이션 생성 성공: id={}, code={}", station.getId(), station.getCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(station);
    }
}
