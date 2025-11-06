package com.ohsang.petcareai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final RestTemplate restTemplate;

    /**
     * Python AI 서버 연결 테스트용 API
     * GET /api/analysis/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> testAiServerConnection() {

        String aiServerUrl = "http://localhost:5001/";
        try {
            String response = restTemplate.getForObject(aiServerUrl, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI 서버 연결에 실패했습니다: " + e.getMessage());
        }
    }


    /**
     * 실제 품종 분석 요청 API
     * Flutter 앱으로부터 이미지를 받아 Python 서버로 전달(포워딩)합니다.
     * POST /api/analysis/breed
     */


    @PostMapping("/breed")
    public ResponseEntity<String> analyzeBreed(
            @RequestParam("file") MultipartFile file) {

        String aiServerUrl = "http://localhost:5001/analyze";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        try {
            // Flutter에서 받은 파일을 Python 서버로 보낼 수 있게 재포장
            ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileAsResource);

            // RestTemplate으로 Python 서버에 POST 요청 (파일 첨부)
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    aiServerUrl,
                    requestEntity,
                    String.class
            );

            // Python 서버의 응답(JSON 문자열)을 Flutter 앱에게 그대로 반환
            return response;

        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI 서버 요청 중 오류 발생: " + e.getMessage());
        }
    }
}