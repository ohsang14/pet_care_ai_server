package com.ohsang.petcareai.dto;

import com.ohsang.petcareai.domain.Dog;
import java.time.LocalDate;
import lombok.Getter;

@Getter // JSON으로 변환하려면 Getter가 필요합니다.
public class DogResponseDto {

    private Long id;
    private String name;
    private LocalDate birthDate;
    // member 정보는 제외하여 비밀번호 노출을 원천 차단

    // Dog(Entity)를 DogResponseDto(DTO)로 변환하는 생성자
    public DogResponseDto(Dog dog) {
        this.id = dog.getId();
        this.name = dog.getName();
        this.birthDate = dog.getBirthDate();
    }
}
