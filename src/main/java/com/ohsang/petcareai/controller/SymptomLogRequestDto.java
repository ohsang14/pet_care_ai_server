package com.ohsang.petcareai.dto;

import com.ohsang.petcareai.domain.Dog;
import com.ohsang.petcareai.domain.SymptomLog;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class SymptomLogRequestDto {

    private String symptom; // 주요 증상
    private String memo; // 상세 메모
    private LocalDateTime logDate; // 사용자가 날짜를 직접 선택할 경우

    // DTO를 엔티티로 변환하는 헬퍼 메서드
    public SymptomLog toEntity(Dog dog) {
        SymptomLog log = new SymptomLog();
        log.setDog(dog);
        log.setSymptom(this.symptom);
        log.setMemo(this.memo);

        // 사용자가 날짜를 보내지 않으면 서버 시간을, 보내면 그 시간을 사용
        log.setLogDate(this.logDate != null ? this.logDate : LocalDateTime.now());

        return log;
    }
}