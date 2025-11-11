package com.ohsang.petcareai.service;

import com.ohsang.petcareai.domain.CatBreedInfo;
import com.ohsang.petcareai.dto.AiResponseDto;
import com.ohsang.petcareai.dto.AnalysisResponseDto;
import com.ohsang.petcareai.dto.CatApiImageDto; // 1. 이미지 DTO import 추가
import com.ohsang.petcareai.dto.CatApiResponseDto;
import com.ohsang.petcareai.repository.CatBreedInfoRepository;
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
public class CatAnalysisService {

    private final RestTemplate restTemplate;
    private final CatBreedInfoRepository catBreedInfoRepository;

    @Value("${cat.api.key}")
    private String catApiKey;

    private final String aiServerUrl = "http://localhost:5001/analyze";
    private final String catApiSearchUrl = "https://api.thecatapi.com/v1/breeds/search?q=";
    private final String catApiImageUrl = "https://api.thecatapi.com/v1/images/"; // 2. 이미지 API 주소

    /**
     * AI 서버, 고양이 DB, The Cat API 3가지를 연동하여 분석 결과를 조합합니다.
     */
    public List<AnalysisResponseDto> analyzeImage(MultipartFile file) throws IOException {

        // 1. AI 서버 호출
        AiResponseDto[] aiResults = callAiServer(file);
        List<AnalysisResponseDto> finalResponseList = new ArrayList<>();

        if (aiResults != null) {
            for (AiResponseDto aiResult : aiResults) {
                String breedNameEn = aiResult.getBreed_name_en();

                // 2. 고양이 DB(cat_breed_info)에서 한국어 이름 및 'API 검색어' 조회
                Optional<CatBreedInfo> breedInfoOptional =
                        catBreedInfoRepository.findByBreedNameEn(breedNameEn);

                CatBreedInfo breedInfo = breedInfoOptional.orElse(null);
                CatApiResponseDto catApiInfo = null;

                // 3. ⭐️ 핵심 로직 ⭐️
                // DB에 'api_search_term'이 설정된 품종(Breed)인 경우에만 API 호출
                if (breedInfo != null && breedInfo.getApiSearchTerm() != null) {
                    // 'Egyptian Mau' 등으로 The Cat API 호출
                    catApiInfo = callTheCatApi(breedInfo.getApiSearchTerm());
                } else {
                    // 'tabby' 등은 API 호출 안 함 (catApiInfo는 null 유지)
                }

                // 4. AnalysisResponseDto로 최종 조합
                // (catApiInfo가 null이더라도 createResponseDto가 처리)
                AnalysisResponseDto finalDto = createResponseDto(aiResult, breedInfo, catApiInfo);
                finalResponseList.add(finalDto);
            }
        }
        return finalResponseList;
    }

    /**
     * Python AI 서버를 호출합니다. (이 코드는 변경 없음)
     */
    private AiResponseDto[] callAiServer(MultipartFile file) throws IOException {
        // ... (기존 코드와 동일) ...
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
     * The Cat API를 호출하여 품종 부가 정보를 가져옵니다. (로직 수정)
     */
    private CatApiResponseDto callTheCatApi(String breedApiName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", catApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 4. 첫 번째 호출: 품종 검색 (참조 ID 획득)
            ResponseEntity<CatApiResponseDto[]> response = restTemplate.exchange(
                    catApiSearchUrl + breedApiName, // 품종 검색 API
                    HttpMethod.GET,
                    entity,
                    CatApiResponseDto[].class
            );

            if (response.getBody() != null && response.getBody().length > 0) {
                CatApiResponseDto breedInfo = response.getBody()[0]; // 첫 번째 검색 결과

                // 5. 두 번째 호출: 참조 ID로 실제 이미지 URL 획득
                if (breedInfo.getReferenceImageId() != null) {
                    String imageUrl = callTheCatImageApi(breedInfo.getReferenceImageId());
                    breedInfo.setImageUrl(imageUrl); // 획득한 URL을 DTO에 세팅
                }

                return breedInfo;
            } else {
                return null; // 검색 결과 없음
            }
        } catch (Exception e) {
            System.out.println("The Cat API 호출 오류: " + e.getMessage());
            return null; // 호출 실패
        }
    }

    // 6. 이미지 ID로 이미지 URL을 조회하는 새 헬퍼 메서드
    private String callTheCatImageApi(String imageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", catApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // CatApiImageDto: 'url' 필드만 있는 DTO (재활용)
            ResponseEntity<CatApiImageDto> response = restTemplate.exchange(
                    catApiImageUrl + imageId, // 이미지 조회 API
                    HttpMethod.GET,
                    entity,
                    CatApiImageDto.class
            );

            if (response.getBody() != null) {
                return response.getBody().getUrl(); // "url" 값 반환
            }
            return null;
        } catch (Exception e) {
            System.out.println("The Cat Image API 호출 오류: " + e.getMessage());
            return null;
        }
    }


    /**
     * 3가지 정보를 AnalysisResponseDto로 조합합니다. (로직 수정)
     */
    private AnalysisResponseDto createResponseDto(AiResponseDto aiResult, CatBreedInfo breedInfo, CatApiResponseDto catApiInfo) {
        AnalysisResponseDto dto = new AnalysisResponseDto();
        dto.setBreedNameEn(aiResult.getBreed_name_en().replace('_', ' '));
        dto.setScore(aiResult.getScore());

        if (breedInfo != null) {
            dto.setBreedNameKo(breedInfo.getBreedNameKo()); // 고양이 한글 이름
        } else {
            dto.setBreedNameKo(dto.getBreedNameEn()); // (정보 없으면 영어 이름)
        }

        if (catApiInfo != null) {
            // 7. catApiInfo.getImage().getUrl() 대신 catApiInfo.getImageUrl() 사용
            dto.setImageUrl(catApiInfo.getImageUrl());
            dto.setTemperament(catApiInfo.getTemperament());
            dto.setLifeSpan(catApiInfo.getLifeSpan());
        } else {
            // 'tabby' 처럼 catApiInfo가 null인 경우
            dto.setImageUrl(null);
            dto.setTemperament("정보 없음");
            dto.setLifeSpan("정보 없음");
        }

        return dto;
    }
}