package com.ohsang.petcareai.service;

import com.ohsang.petcareai.domain.BreedInfo;
import com.ohsang.petcareai.dto.AiResponseDto;
import com.ohsang.petcareai.dto.AnalysisResponseDto;
import com.ohsang.petcareai.dto.DogApiResponseDto;
import com.ohsang.petcareai.repository.BreedInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final RestTemplate restTemplate;
    private final BreedInfoRepository breedInfoRepository;

    // application.properties에 저장한 API 키를 불러옵니다.
    @Value("${dog.api.key}")
    private String dogApiKey;

    private final String aiServerUrl = "http://localhost:5001/analyze";
    private final String dogApiUrl = "https://api.thedogapi.com/v1/breeds/search?q=";

    /**
     * 이미지 분석의 모든 과정을 처리하는 메인 메소드
     */
    public List<AnalysisResponseDto> analyzeImage(MultipartFile file) throws IOException {

        // 1. Python AI 서버에 이미지 전송 및 '순수 AI 결과' 받기
        AiResponseDto[] aiResults = callAiServer(file);

        List<AnalysisResponseDto> finalResponseList = new ArrayList<>();

        if (aiResults != null) {
            for (AiResponseDto aiResult : aiResults) {

                String breedNameEn = aiResult.getBreed_name_en();

                // 2. MySQL DB에서 '한국어 이름' 조회
                Optional<BreedInfo> breedInfoOptional =
                        breedInfoRepository.findByBreedNameEn(breedNameEn);

                // 3. The Dog API에서 '이미지/부가정보' 조회
                String searchName = breedNameEn.replace('_', ' ');
                DogApiResponseDto dogApiInfo = callTheDogApi(searchName);

                AnalysisResponseDto finalDto;
                if (breedInfoOptional.isPresent()) {
                    // 4-A. DB 정보 O: (AI 결과 + DB 정보 + Dog API 정보) 조합
                    finalDto = new AnalysisResponseDto(aiResult, breedInfoOptional.get(), dogApiInfo);
                } else {
                    // 4-B. DB 정보 X: (AI 결과 + Dog API 정보) 조합 (Fallback)
                    finalDto = new AnalysisResponseDto(aiResult, null, dogApiInfo);
                }
                finalResponseList.add(finalDto);
            }
        }
        return finalResponseList;
    }

    /**
     * Python AI 서버 호출
     */
    private AiResponseDto[] callAiServer(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("file", fileAsResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<AiResponseDto[]> response = restTemplate.postForEntity(
                aiServerUrl,
                requestEntity,
                AiResponseDto[].class
        );
        return response.getBody();
    }

    /**
     * 'The Dog API'를 호출하는 새 함수
     */
    private DogApiResponseDto callTheDogApi(String breedName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", dogApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<DogApiResponseDto[]> response = restTemplate.exchange(
                    dogApiUrl + breedName, // e.g., .../search?q=Maltese dog
                    HttpMethod.GET,
                    entity,
                    DogApiResponseDto[].class // 응답을 DogApiResponseDto '배열'로 받음
            );

            if (response.getBody() != null && response.getBody().length > 0) {
                return response.getBody()[0]; // 첫 번째(가장 정확한) 결과만 사용
            } else {
                return null; // The Dog API에 검색 결과가 없는 경우
            }
        } catch (Exception e) {
            System.out.println("The Dog API 호출 오류: " + e.getMessage());
            return null; // 에러 발생 시
        }
    }
}