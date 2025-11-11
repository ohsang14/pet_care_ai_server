package com.ohsang.petcareai.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "symptom_log")
@Getter
@Setter
public class SymptomLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime logDate; // 기록 날짜

    @Column(nullable = false)
    private String symptom; // 주요 증상

    @Lob // 텍스트를 저장 (TEXT 타입)
    private String memo; // 상세 메모

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "dog_id", nullable = false)
    @JsonIgnore // API 응답 시 Dog 객체는 제외 (무한 순환 방지)
    private Dog dog;

    // (엔티티가 생성되거나 업데이트될 때 자동으로 현재 시간을 logDate에 설정)
    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        if (this.logDate == null) {
            this.logDate = LocalDateTime.now();
        }
    }
}