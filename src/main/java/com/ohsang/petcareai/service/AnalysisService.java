package com.ohsang.petcareai.service;

import com.ohsang.petcareai.domain.BreedInfo;
import com.ohsang.petcareai.dto.AiResponseDto;
import com.ohsang.petcareai.dto.AnalysisResponseDto;
import com.ohsang.petcareai.dto.DogApiImageDto; // 👈 추가
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

    @Value("${dog.api.key}")
    private String dogApiKey;

    private final String aiServerUrl = "http://localhost:5001/analyze";
    private final String dogApiUrl = "https://api.thedogapi.com/v1/breeds/search?q=";
    // 👇 [추가] 이미지 상세 조회용 URL
    private final String dogApiImageUrl = "https://api.thedogapi.com/v1/images/";

    public List<AnalysisResponseDto> analyzeImage(MultipartFile file) throws IOException {
        AiResponseDto[] aiResults = callAiServer(file);
        List<AnalysisResponseDto> finalResponseList = new ArrayList<>();

        if (aiResults != null) {
            for (AiResponseDto aiResult : aiResults) {
                String breedNameEn = aiResult.getBreed_name_en();
                Optional<BreedInfo> breedInfoOptional = breedInfoRepository.findByBreedNameEn(breedNameEn);

                String searchName = breedNameEn.replace('_', ' ');
                BreedInfo breedInfo = breedInfoOptional.orElse(null);

                if (breedInfo != null && breedInfo.getApiSearchTerm() != null) {
                    searchName = breedInfo.getApiSearchTerm();
                }

                // API 호출
                DogApiResponseDto dogApiInfo = callTheDogApi(searchName);

                AnalysisResponseDto finalDto;
                if (breedInfo != null) {
                    finalDto = new AnalysisResponseDto(aiResult, breedInfo, dogApiInfo);
                } else {
                    finalDto = new AnalysisResponseDto(aiResult, null, dogApiInfo);
                }
                finalResponseList.add(finalDto);
            }
        }
        return finalResponseList;
    }

    private AiResponseDto[] callAiServer(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() { return file.getOriginalFilename(); }
        };
        body.add("file", fileAsResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<AiResponseDto[]> response = restTemplate.postForEntity(aiServerUrl, requestEntity, AiResponseDto[].class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The Dog API 호출 (업그레이드됨)
     */
    private DogApiResponseDto callTheDogApi(String breedName) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", dogApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<DogApiResponseDto[]> response = restTemplate.exchange(
                    dogApiUrl + breedName,
                    HttpMethod.GET,
                    entity,
                    DogApiResponseDto[].class
            );

            if (response.getBody() != null && response.getBody().length > 0) {
                DogApiResponseDto result = response.getBody()[0];

                // ⭐️ [핵심 수정] 이미지가 없고 참조 ID만 있다면? -> 이미지 API 재호출!
                if (result.getImage() == null && result.getReferenceImageId() != null) {
                    String imageUrl = callTheDogImageApi(result.getReferenceImageId());
                    if (imageUrl != null) {
                        // 가짜 ImageDto를 만들어서 넣어줍니다.
                        DogApiImageDto imageDto = new DogApiImageDto();
                        imageDto.setUrl(imageUrl);
                        result.setImage(imageDto);
                    }
                }
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("The Dog API 호출 오류: " + e.getMessage());
            return null;
        }
    }

    // 👇 [추가] 이미지 ID로 실제 URL을 가져오는 메서드
    private String callTheDogImageApi(String imageId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", dogApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<DogApiImageDto> response = restTemplate.exchange(
                    dogApiImageUrl + imageId,
                    HttpMethod.GET,
                    entity,
                    DogApiImageDto.class
            );
            if (response.getBody() != null) {
                return response.getBody().getUrl();
            }
        } catch (Exception e) {
            System.out.println("The Dog Image API 오류: " + e.getMessage());
        }
        return null;
    }
}