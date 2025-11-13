package com.ohsang.petcareai.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // 👈 1. import 추가
import com.ohsang.petcareai.domain.HealthCheck;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HealthCheckResponseDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("checkDate")
    private LocalDateTime checkDate;

    @JsonProperty("totalScore")
    private int totalScore;

    @JsonProperty("answerStep1Appetite")
    private String answerStep1Appetite;

    @JsonProperty("answerStep2Activity")
    private String answerStep2Activity;

    @JsonProperty("answerStep3Digestive")
    private String answerStep3Digestive;

    @JsonProperty("answerStep4Urinary")
    private String answerStep4Urinary;

    @JsonProperty("answerStep5Skin")
    private String answerStep5Skin;

    // 엔티티를 DTO로 변환하는 생성자 (수정 없음)
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