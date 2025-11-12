package com.ohsang.petcareai.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_check")
@Getter
@Setter
public class HealthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "check_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime checkDate;

    @Column(nullable = false)
    private int totalScore;

    // 5단계 질문 답변을 저장할 컬럼들
    @Column(nullable = false)
    private String answerStep1Appetite;

    @Column(nullable = false)
    private String answerStep2Activity;

    @Column(nullable = false)
    private String answerStep3Digestive;

    @Column(nullable = false)
    private String answerStep4Urinary;

    @Column(nullable = false)
    private String answerStep5Skin;

    // HealthCheck는 Dog에 속합니다 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    @JsonIgnore // API 응답 시 Dog 객체는 제외 (무한 순환 방지)
    private Dog dog;

    // 저장 전 날짜 자동 입력
    @PrePersist
    public void prePersist() {
        this.checkDate = LocalDateTime.now();
    }
}