package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.domain.Dog;
import com.ohsang.petcareai.domain.SymptomLog;
import com.ohsang.petcareai.dto.SymptomLogRequestDto;
import com.ohsang.petcareai.dto.SymptomLogResponseDto;
import com.ohsang.petcareai.repository.DogRepository;
import com.ohsang.petcareai.repository.SymptomLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dogs/{dogId}/symptoms") // 1. 기본 URL 경로
@RequiredArgsConstructor
public class SymptomLogController {

    private final SymptomLogRepository symptomLogRepository;
    private final DogRepository dogRepository;

    /**
     * 특정 반려견의 모든 증상 기록 조회 (최신순)
     */
    @GetMapping
    public ResponseEntity<List<SymptomLogResponseDto>> getSymptomLogs(@PathVariable Long dogId) {

        // 반려견이 존재하는지 먼저 확인
        if (!dogRepository.existsById(dogId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 2. 리포지토리의 쿼리 메서드 호출 (최신순 정렬)
        List<SymptomLog> logs = symptomLogRepository.findByDogIdOrderByLogDateDesc(dogId);

        // 3. 엔티티 리스트 -> DTO 리스트로 변환
        List<SymptomLogResponseDto> dtoList = logs.stream()
                .map(SymptomLogResponseDto::new) // .map(log -> new SymptomLogResponseDto(log))와 동일
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    /**
     * 특정 반려견의 새 증상 기록 추가
     */
    @PostMapping
    public ResponseEntity<SymptomLogResponseDto> addSymptomLog(
            @PathVariable Long dogId,
            @RequestBody SymptomLogRequestDto requestDto) {

        // 4. dogId로 반려견 엔티티 조회
        Optional<Dog> optionalDog = dogRepository.findById(dogId);
        if (optionalDog.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 반려견 없음
        }

        Dog dog = optionalDog.get();

        // 5. DTO를 엔티티로 변환 (이때 Dog 객체를 넘겨줌)
        SymptomLog newLog = requestDto.toEntity(dog);

        // 6. DB에 저장
        SymptomLog savedLog = symptomLogRepository.save(newLog);

        // 7. 저장된 엔티티를 다시 응답 DTO로 변환하여 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(new SymptomLogResponseDto(savedLog));
    }
}