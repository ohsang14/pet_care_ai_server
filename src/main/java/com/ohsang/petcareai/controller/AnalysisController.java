package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.dto.AnalysisResponseDto;
import com.ohsang.petcareai.service.AnalysisService;
import com.ohsang.petcareai.service.CatAnalysisService; // 1. 고양이 서비스 import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    // 2. 강아지 서비스 (기존 AnalysisService)
    private final AnalysisService dogAnalysisService;

    // 3. 고양이 서비스 (새로 만든 CatAnalysisService)
    private final CatAnalysisService catAnalysisService;

    /**
     * 강아지 품종 분석 API
     */
    @PostMapping("/dog") // 4. 엔드포인트를 /breed 에서 /dog 로 변경
    public ResponseEntity<List<AnalysisResponseDto>> analyzeDogBreed(
            @RequestParam("file") MultipartFile file) {

        try {
            // 5. 강아지 서비스 호출
            List<AnalysisResponseDto> results = dogAnalysisService.analyzeImage(file);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 고양이 품종 분석 API
     */
    @PostMapping("/cat") // 6. 고양이용 /cat 엔드포인트 신설
    public ResponseEntity<List<AnalysisResponseDto>> analyzeCatBreed(
            @RequestParam("file") MultipartFile file) {

        try {
            // 7. 고양이 서비스 호출
            List<AnalysisResponseDto> results = catAnalysisService.analyzeImage(file);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}