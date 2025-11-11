package com.ohsang.petcareai.dto;

import com.ohsang.petcareai.domain.BreedInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

// Flutter 앱에 최종적으로 보낼 '완성본' DTO
@Data
@NoArgsConstructor
public class AnalysisResponseDto {

    // (이 필드명은 Flutter의 AnalysisResult 모델과 일치해야 함: camelCase)
    private String breedNameEn; // AI 영어 이름
    private String breedNameKo; // DB 한국어 이름
    private String imageUrl;    // Dog API 이미지 URL
    private double score;       // AI 확률
    private String temperament; // Dog API 성격
    private String lifeSpan;    // Dog API 수명

    // [생성자 1] 모든 정보 조합 (AI + DB + Dog API)
    public AnalysisResponseDto(AiResponseDto aiResult, BreedInfo breedInfo, DogApiResponseDto dogApiInfo) {
        this.breedNameEn = aiResult.getBreed_name_en().replace('_', ' '); // Python의 snake_case -> 공백
        this.score = aiResult.getScore();

        // 1. DB에서 찾은 한국어 이름 설정
        if (breedInfo != null) {
            this.breedNameKo = breedInfo.getBreedNameKo();
        } else {
            this.breedNameKo = this.breedNameEn; // DB에 없으면 영어 이름 사용
        }

        // 2. The Dog API에서 찾은 부가 정보 설정
        if (dogApiInfo != null) {
            this.imageUrl = (dogApiInfo.getImage() != null) ? dogApiInfo.getImage().getUrl() : null;
            this.temperament = dogApiInfo.getTemperament();
            this.lifeSpan = dogApiInfo.getLifeSpan();
        } else {
            // The Dog API에 정보가 없는 경우
            this.imageUrl = null;
            this.temperament = "정보 없음";
            this.lifeSpan = "정보 없음";
        }
    }

    // [생성자 2] Fallback (AI 정보만 있을 때)
    public AnalysisResponseDto(AiResponseDto aiResult) {
        this.breedNameEn = aiResult.getBreed_name_en().replace('_', ' ');
        this.breedNameKo = this.breedNameEn; // 모를 경우 그냥 영어 이름
        this.score = aiResult.getScore();
        this.imageUrl = null;
        this.temperament = "정보 없음";
        this.lifeSpan = "정보 없음";
    }
}