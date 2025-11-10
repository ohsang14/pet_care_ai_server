package com.ohsang.petcareai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiResponseDto {
    private String breed_name_en;
    private Double score;

    public AiResponseDto(){

    }
}
