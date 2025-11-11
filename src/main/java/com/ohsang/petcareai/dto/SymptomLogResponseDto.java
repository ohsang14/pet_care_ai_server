package com.ohsang.petcareai.dto;

import com.ohsang.petcareai.domain.SymptomLog;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class SymptomLogResponseDto {

    private Long id;
    private LocalDateTime logDate;
    private String symptom;
    private String memo;

    // 엔티티를 DTO로 변환하는 생성자
    public SymptomLogResponseDto(SymptomLog log) {
        this.id = log.getId();
        this.logDate = log.getLogDate();
        this.symptom = log.getSymptom();
        this.memo = log.getMemo();
    }
}