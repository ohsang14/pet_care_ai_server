package com.ohsang.petcareai.dto;

import com.ohsang.petcareai.domain.Dog;
import com.ohsang.petcareai.domain.HealthCheck;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthCheckRequestDto {

    // Flutter가 계산한 최종 점수
    private int totalScore;

    // Flutter가 보내는 5단계 답변 텍스트
    private String answerStep1Appetite;
    private String answerStep2Activity;
    private String answerStep3Digestive;
    private String answerStep4Urinary;
    private String answerStep5Skin;

    // 이 DTO를 엔티티로 변환하는 헬퍼 메서드
    public HealthCheck toEntity(Dog dog) {
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setDog(dog);
        healthCheck.setTotalScore(this.totalScore);
        healthCheck.setAnswerStep1Appetite(this.answerStep1Appetite);
        healthCheck.setAnswerStep2Activity(this.answerStep2Activity);
        healthCheck.setAnswerStep3Digestive(this.answerStep3Digestive);
        healthCheck.setAnswerStep4Urinary(this.answerStep4Urinary);
        healthCheck.setAnswerStep5Skin(this.answerStep5Skin);
        // (참고: checkDate는 @PrePersist에 의해 자동 생성됨)
        return healthCheck;
    }
}