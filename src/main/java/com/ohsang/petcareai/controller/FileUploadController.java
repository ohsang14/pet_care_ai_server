package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService; // 1. 파일 저장 서비스 주입

    /**
     * Flutter 앱에서 프로필 이미지를 업로드하는 API
     * @param file (MultipartFile)
     * @return {"imageUrl": "/images/저장된파일이름.jpg"}
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        // 2. 파일이 비어있는지 확인
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "업로드할 파일이 비어있습니다."));
        }

        try {
            // 3. 서비스를 호출하여 파일을 저장하고, URL을 받음
            String fileUrl = fileStorageService.storeFile(file);

            // 4. Flutter가 사용하기 쉽도록 JSON 형태로 URL 반환
            //    {"imageUrl": "/images/abc-123.jpg"}
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("imageUrl", fileUrl));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "파일 저장 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}