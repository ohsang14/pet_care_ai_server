package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.domain.Dog;
import com.ohsang.petcareai.domain.HealthCheck;
import com.ohsang.petcareai.dto.HealthCheckRequestDto;
import com.ohsang.petcareai.dto.HealthCheckResponseDto;
import com.ohsang.petcareai.repository.DogRepository;
import com.ohsang.petcareai.repository.HealthCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dogs/{dogId}/health-checks") // 1. 공통 URL 경로
@RequiredArgsConstructor
public class HealthCheckController {

    private final HealthCheckRepository healthCheckRepository;
    private final DogRepository dogRepository; // 반려견 존재 여부 확인용

    /**
     * 특정 반려견의 모든 건강 체크 기록 조회 (GET)
     */
    @GetMapping
    public ResponseEntity<List<HealthCheckResponseDto>> getHealthChecks(@PathVariable Long dogId) {

        // 반려견이 존재하는지 확인
        if (!dogRepository.existsById(dogId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 2. 리포지토리 호출 (최신순 정렬)
        List<HealthCheck> checks = healthCheckRepository.findByDogIdOrderByCheckDateDesc(dogId);

        // 3. 엔티티 리스트 -> DTO 리스트로 변환
        List<HealthCheckResponseDto> dtoList = checks.stream()
                .map(HealthCheckResponseDto::new) // .map(check -> new HealthCheckResponseDto(check))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    /**
     * 특정 반려견의 새 건강 체크 결과 저장 (POST)
     */
    @PostMapping
    public ResponseEntity<HealthCheckResponseDto> addHealthCheck(
            @PathVariable Long dogId,
            @RequestBody HealthCheckRequestDto requestDto) {

        // 4. 반려견 엔티티 조회
        Optional<Dog> optionalDog = dogRepository.findById(dogId);
        if (optionalDog.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 반려견 없음
        }
        Dog dog = optionalDog.get();

        // 5. DTO를 엔티티로 변환
        HealthCheck newCheck = requestDto.toEntity(dog);

        // 6. DB에 저장
        HealthCheck savedCheck = healthCheckRepository.save(newCheck);

        // 7. 저장된 객체를 응답 DTO로 변환하여 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(new HealthCheckResponseDto(savedCheck));
    }
}