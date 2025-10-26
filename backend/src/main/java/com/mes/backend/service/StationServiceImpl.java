package com.mes.backend.service;

import com.mes.backend.dto.request.CreateStationRequest;
import com.mes.backend.dto.response.StationResponse;
import com.mes.backend.entity.Process;
import com.mes.backend.entity.Station;
import com.mes.backend.repository.ProcessRepository;
import com.mes.backend.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 스테이션 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;
    private final ProcessRepository processRepository;

    @Override
    @Transactional
    public StationResponse createStation(CreateStationRequest request) {
        log.info("스테이션 생성 요청: code={}, processId={}",
                request.getCode(), request.getProcessId());

        // 1. 스테이션 코드 중복 체크
        if (stationRepository.existsByCode(request.getCode())) {
            log.warn("스테이션 코드 중복: code={}", request.getCode());
            throw new IllegalArgumentException("이미 존재하는 스테이션 코드입니다: " + request.getCode());
        }

        // 2. 공정 존재 확인
        Process process = processRepository.findById(request.getProcessId())
                .orElseThrow(() -> {
                    log.warn("공정을 찾을 수 없음: processId={}", request.getProcessId());
                    return new IllegalArgumentException("공정을 찾을 수 없습니다: " + request.getProcessId());
                });

        // 3. 스테이션 생성 및 저장
        Station station = Station.builder()
                .name(request.getName())
                .code(request.getCode())
                .process(process)
                .status(request.getStatus())
                .build();

        Station savedStation = stationRepository.save(station);

        log.info("스테이션 생성 완료: id={}, code={}", savedStation.getId(), savedStation.getCode());

        return StationResponse.from(savedStation);
    }

    @Override
    public StationResponse getStationById(Long id) {
        log.info("스테이션 조회 요청: id={}", id);

        // Fetch Join으로 공정 정보 함께 로드
        Station station = stationRepository.findByIdWithProcess(id)
                .orElseThrow(() -> {
                    log.warn("스테이션을 찾을 수 없음: id={}", id);
                    return new IllegalArgumentException("스테이션을 찾을 수 없습니다: " + id);
                });

        log.info("스테이션 조회 완료: id={}, code={}", station.getId(), station.getCode());
        return StationResponse.from(station);
    }

    @Override
    public List<StationResponse> getAllStations() {
        log.info("전체 스테이션 목록 조회 요청");

        // Fetch Join으로 N+1 문제 해결
        List<Station> stations = stationRepository.findAllWithProcess();

        log.info("스테이션 목록 조회 완료: count={}", stations.size());

        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<StationResponse> getStationsByProcessId(Long processId) {
        log.info("공정별 스테이션 목록 조회 요청: processId={}", processId);

        // 공정 존재 확인
        if (!processRepository.existsById(processId)) {
            log.warn("공정을 찾을 수 없음: processId={}", processId);
            throw new IllegalArgumentException("공정을 찾을 수 없습니다: " + processId);
        }

        List<Station> stations = stationRepository.findByProcessId(processId);

        log.info("공정별 스테이션 목록 조회 완료: processId={}, count={}", processId, stations.size());

        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }
}
