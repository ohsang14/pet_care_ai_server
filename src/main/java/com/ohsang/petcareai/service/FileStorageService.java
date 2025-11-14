package com.ohsang.petcareai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    // 1. 이미지를 저장할 경로 (project에 'uploads'에 둘 예정)
    private final String uploadDir = "uploads/";

    public FileStorageService() {
        // 2. 서버 시작때 'uploads' 파일 없으면 자동으로 생성
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }
    /**
     * 파일을 서버에 저장하고, 접근 가능한 URL 경로를 반환합니다.
     * @param file (MultipartFile)
     * @return /images/저장된파일이름.jpg (예: /images/abc-123.jpg)
     * @throws IOException
     */
    public String storeFile(
            MultipartFile file) throws IOException {
        // 3. 파일 이름이 겹치지 않도록 UUID와 원본 파일명을 조합
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        // 4. 저장할 전체 경로 (예: uploads/abc-123.jpg)
        Path destinationPath = Paths.get(uploadDir + File.separator + storedFileName);

        // 5. 파일을 해당 경로에 저장
        Files.copy(file.getInputStream(), destinationPath);

        // 6. Flutter(클라이언트)가 접근할 수 있는 URL 경로 반환
        // (주의: "/images/"는 실제 폴더 이름("/uploads/")와 다릅니다. 다음 단계에서 매핑할 것입니다.)
        return "/images/" + storedFileName;
    }
}
