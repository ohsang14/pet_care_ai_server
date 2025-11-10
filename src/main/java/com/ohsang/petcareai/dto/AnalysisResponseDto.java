package com.ohsang.petcareai.dto;

import com.ohsang.petcareai.domain.BreedInfo;
import lombok.Getter;

// Flutter 앱으로 보낼 최종 응답 DTO
// Flutter의 'analysis_result.dart' 모델과 1:1로 대응됩니다.
@Getter
public class AnalysisResponseDto {

    // 👇 1. 'breedNameEn' 필드를 추가!
    private String breedNameEn; // Flutter 모델의 'breedNameEn'
    private String breedNameKo; // Flutter 모델의 'breedNameKo'
    private String imageUrl;    // Flutter 모델의 'imageUrl'
    private Double score;       // Flutter 모델의 'score'

    // 2. (DB 정보 + AI 정보) 조합 생성자 수정
    public AnalysisResponseDto(BreedInfo breedInfo, AiResponseDto aiResult) {
        this.breedNameEn = aiResult.getBreed_name_en().replace('_', ' '); // 👈 AI 결과에서 영어 이름 추가
        this.breedNameKo = breedInfo.getBreedNameKo();
        this.imageUrl = breedInfo.getImageUrl();
        this.score = aiResult.getScore();
    }

    // 3. (Fallback) 생성자 수정
    public AnalysisResponseDto(AiResponseDto aiResult) {
        this.breedNameEn = aiResult.getBreed_name_en().replace('_', ' '); // 👈 AI 결과에서 영어 이름 추가
        this.breedNameKo = aiResult.getBreed_name_en().replace('_', ' '); // 한국어 이름이 없으니 영어 이름이라도
        this.imageUrl = null;
        this.score = aiResult.getScore();
    }
}