package com.ohsang.petcareai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    // 1. 방금 Bean으로 등록한 RestTemplate을 주입받습니다.
    private final RestTemplate restTemplate;

    /**
     * Python AI 서버 연결 테스트용 API
     * GET /api/analysis/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> testAiServerConnection() {

        // 2. Python 서버의 주소
        String aiServerUrl = "http://localhost:5001/";

        try {
            // 3. RestTemplate을 사용해 Python 서버에 GET 요청을 보냄
            String response = restTemplate.getForObject(aiServerUrl, String.class);

            // 4. Python 서버가 보낸 응답을 그대로 클라이언트(Postman)에게 전달
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 5. Python 서버에 연결 실패 시 에러 메시지 반환
            return ResponseEntity.status(500).body("AI 서버 연결에 실패했습니다: " + e.getMessage());
        }
    }
}

/**
        ## ## 3단계: 테스트하기 (가장 중요!)

이제 두 서버가 모두 켜져 있어야 합니다.
1.  **Python 서버 실행:** 터미널에서 `python3 app.py`를 실행 (포트 5001)
2.  **Spring Boot 서버 재시작:** IntelliJ에서 (▶️) 버튼을 눌러 재시작 (포트 8080)

두 서버가 모두 켜졌다면, **Postman**을 열고 **Spring Boot 서버**의 새 API를 호출합니다.

        * **메소드:** **`GET`**
        * **URL:** `http://localhost:8080/api/analysis/test`

        #### **예상 결과:**
        * **Status:** `200 OK`
        * **Body:** Python 서버가 보낸 메시지가 그대로 전달되어야 합니다.
    ```
            "안녕하세요! PetCare AI Python 서버입니다."


*/