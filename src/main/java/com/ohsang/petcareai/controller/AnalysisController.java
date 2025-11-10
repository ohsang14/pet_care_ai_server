package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.dto.AnalysisResponseDto;
import com.ohsang.petcareai.service.AnalysisService; // 👈 1. RestTemplate 대신 Service를 import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 👈 2. MultipartFile import

import java.util.List; // 👈 3. List import

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    // 4. RestTemplate 대신 AnalysisService를 주입받음
    private final AnalysisService analysisService;

    /**
     * 실제 품종 분석 요청 API
     * 이제 Controller는 '안내 데스크' 역할만 하고,
     * 모든 복잡한 처리는 'analysisService'가 담당합니다.
     */
    @PostMapping("/breed")
    public ResponseEntity<List<AnalysisResponseDto>> analyzeBreed(
            @RequestParam("file") MultipartFile file) {

        try {
            // 5. '작업반장'에게 이미지 파일을 넘기고, '최종 완성본' DTO 리스트를 받음
            List<AnalysisResponseDto> results = analysisService.analyzeImage(file);

            // 6. 성공 응답 반환
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            // 7. 에러 처리
            e.printStackTrace(); // 👈 서버 로그에 에러를 찍어보는 것이 좋습니다.
            return ResponseEntity.status(500).body(null); // 👈 null 대신 에러 DTO를 보낼 수도 있습니다.
        }
    }
}