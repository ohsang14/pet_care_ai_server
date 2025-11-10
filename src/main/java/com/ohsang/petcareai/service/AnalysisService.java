package com.ohsang.petcareai.service;

import com.ohsang.petcareai.domain.BreedInfo;
import com.ohsang.petcareai.dto.AiResponseDto;
import com.ohsang.petcareai.dto.AnalysisResponseDto;
import com.ohsang.petcareai.repository.BreedInfoRepository;
import lombok.RequiredArgsConstructor;
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

    // Python AI 서버의 분석 URL
    private final String aiServerUrl = "http://localhost:5001/analyze";

    /**
     * 이미지 분석의 모든 과정을 처리하는 메인 메소드
     */
    public List<AnalysisResponseDto> analyzeImage(MultipartFile file) throws IOException {

        // 1. Python AI 서버에 이미지 전송 및 '순수 AI 결과' 받기
        AiResponseDto[] aiResults = callAiServer(file);

        // 2. 최종적으로 Flutter 앱에 보낼 '완성본 DTO 리스트' 생성
        List<AnalysisResponseDto> finalResponseList = new ArrayList<>();

        // 3. AI 결과를 하나씩 순회하며, DB에서 '추가 정보'를 조회하고 '조합'
        if (aiResults != null) {
            for (AiResponseDto aiResult : aiResults) {

                // 4. AI가 준 영어 이름(e.g., 'Maltese_dog')으로 MySQL DB 조회
                Optional<BreedInfo> breedInfoOptional =
                        breedInfoRepository.findByBreedNameEn(aiResult.getBreed_name_en());

                AnalysisResponseDto finalDto;
                if (breedInfoOptional.isPresent()) {
                    // 5-A. DB에 정보가 있는 경우: (DB 정보 + AI 확률)로 조합
                    BreedInfo breedInfo = breedInfoOptional.get();
                    finalDto = new AnalysisResponseDto(breedInfo, aiResult);
                } else {
                    // 5-B. DB에 정보가 없는 경우: (AI 정보만)으로 조합 (Fallback)
                    finalDto = new AnalysisResponseDto(aiResult);
                }
                finalResponseList.add(finalDto);
            }
        }

        // 6. '최종 완성본' 리스트 반환
        return finalResponseList;
    }

    /**
     * RestTemplate을 사용해 Python AI 서버를 호출하는 내부 함수
     */
    private AiResponseDto[] callAiServer(MultipartFile file) throws IOException {

        // 1. Python 서버로 'multipart/form-data'를 보내기 위한 준비
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 2. Flutter에서 받은 파일을 Python 서버로 보낼 수 있게 재포장
        //    (파일 이름이 없으면 Python 서버가 파일을 인식하지 못할 수 있음)
        ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                // 원본 파일 이름을 사용해야 Python Flask가 제대로 인식
                return file.getOriginalFilename();
            }
        };
        body.add("file", fileAsResource);

        // 3. RestTemplate으로 Python 서버에 POST 요청 (파일 첨부)
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 4. [중요] 응답을 String이 아닌 AiResponseDto '배열'([])로 받음
        ResponseEntity<AiResponseDto[]> response = restTemplate.postForEntity(
                aiServerUrl,
                requestEntity,
                AiResponseDto[].class
        );

        // 5. Python 서버가 보낸 '순수 AI 결과' (JSON 배열) 반환
        return response.getBody();
    }
}