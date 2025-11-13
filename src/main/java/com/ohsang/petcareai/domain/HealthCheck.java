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

    // ⭐️ [수정] Java 필드(camelCase)와 DB 컬럼(snake_case)을 1:1 매핑
    @Column(name = "answer_step1_appetite", nullable = false)
    private String answerStep1Appetite;

    @Column(name = "answer_step2_activity", nullable = false)
    private String answerStep2Activity;

    @Column(name = "answer_step3_digestive", nullable = false)
    private String answerStep3Digestive;

    @Column(name = "answer_step4_urinary", nullable = false)
    private String answerStep4Urinary;

    @Column(name = "answer_step5_skin", nullable = false)
    private String answerStep5Skin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    @JsonIgnore
    private Dog dog;

    @PrePersist
    public void prePersist() {
        this.checkDate = LocalDateTime.now();
    }
}