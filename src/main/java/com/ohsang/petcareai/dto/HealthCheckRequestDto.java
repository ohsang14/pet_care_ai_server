package com.ohsang.petcareai.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // 👈 1. import 추가
import com.ohsang.petcareai.domain.Dog;
import com.ohsang.petcareai.domain.HealthCheck;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthCheckRequestDto {

    @JsonProperty("totalScore") // 👈 2. JSON 키 매핑
    private int totalScore;

    @JsonProperty("answerStep1Appetite") // 👈 3. JSON 키 매핑
    private String answerStep1Appetite;

    @JsonProperty("answerStep2Activity") // 👈 4. JSON 키 매핑
    private String answerStep2Activity;

    @JsonProperty("answerStep3Digestive") // 👈 5. JSON 키 매핑
    private String answerStep3Digestive;

    @JsonProperty("answerStep4Urinary") // 👈 6. JSON 키 매핑
    private String answerStep4Urinary;

    @JsonProperty("answerStep5Skin") // 👈 7. JSON 키 매핑
    private String answerStep5Skin;

    // DTO를 엔티티로 변환하는 헬퍼 메서드 (수정 없음)
    public HealthCheck toEntity(Dog dog) {
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setDog(dog);
        healthCheck.setTotalScore(this.totalScore);
        healthCheck.setAnswerStep1Appetite(this.answerStep1Appetite);
        healthCheck.setAnswerStep2Activity(this.answerStep2Activity);
        healthCheck.setAnswerStep3Digestive(this.answerStep3Digestive);
        healthCheck.setAnswerStep4Urinary(this.answerStep4Urinary);
        healthCheck.setAnswerStep5Skin(this.answerStep5Skin);
        return healthCheck;
    }
}