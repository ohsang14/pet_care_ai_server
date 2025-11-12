package com.ohsang.petcareai.dto;

import com.ohsang.petcareai.domain.HealthCheck;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HealthCheckResponseDto {

    private Long id;
    private LocalDateTime checkDate;
    private int totalScore;

    // 5단계 답변 텍스트
    private String answerStep1Appetite;
    private String answerStep2Activity;
    private String answerStep3Digestive;
    private String answerStep4Urinary;
    private String answerStep5Skin;

    // 엔티티를 DTO로 변환하는 생성자
    public HealthCheckResponseDto(HealthCheck healthCheck) {
        this.id = healthCheck.getId();
        this.checkDate = healthCheck.getCheckDate();
        this.totalScore = healthCheck.getTotalScore();
        this.answerStep1Appetite = healthCheck.getAnswerStep1Appetite();
        this.answerStep2Activity = healthCheck.getAnswerStep2Activity();
        this.answerStep3Digestive = healthCheck.getAnswerStep3Digestive();
        this.answerStep4Urinary = healthCheck.getAnswerStep4Urinary();
        this.answerStep5Skin = healthCheck.getAnswerStep5Skin();
    }
}