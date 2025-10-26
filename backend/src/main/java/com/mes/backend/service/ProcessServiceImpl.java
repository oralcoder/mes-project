package com.mes.backend.service;

import com.mes.backend.dto.request.CreateProcessRequest;
import com.mes.backend.dto.response.ProcessResponse;
import com.mes.backend.entity.Process;
import com.mes.backend.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 공정 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRepository processRepository;

    @Override
    @Transactional
    public ProcessResponse createProcess(CreateProcessRequest request) {
        log.info("공정 생성 요청: name={}, sequence={}", request.getName(), request.getSequence());

        // 중복 체크
        if (processRepository.existsByName(request.getName())) {
            log.warn("공정명 중복: name={}", request.getName());
            throw new IllegalArgumentException("이미 존재하는 공정명입니다: " + request.getName());
        }

        // 저장
        Process process = processRepository.save(request.toEntity());
        log.info("공정 생성 완료: id={}, name={}", process.getId(), process.getName());

        return ProcessResponse.from(process);
    }

    @Override
    public ProcessResponse getProcessById(Long id) {
        log.info("공정 조회 요청: id={}", id);

        Process process = processRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("공정을 찾을 수 없음: id={}", id);
                    return new IllegalArgumentException("공정을 찾을 수 없습니다: " + id);
                });

        log.info("공정 조회 완료: id={}, name={}", process.getId(), process.getName());
        return ProcessResponse.from(process);
    }

    @Override
    public List<ProcessResponse> getAllProcesses() {
        log.info("전체 공정 목록 조회 요청");

        // 순서대로 정렬하여 조회
        List<Process> processes = processRepository.findAllByOrderBySequenceAsc();

        log.info("공정 목록 조회 완료: count={}", processes.size());

        return processes.stream()
                .map(ProcessResponse::from)
                .collect(Collectors.toList());
    }
}
