package com.ohsang.petcareai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

// The Dog API가 반환하는 JSON 배열의 각 항목을 받기 위한 DTO
@Data
@NoArgsConstructor
public class DogApiResponseDto {
    private int id;
    private String name; // 👈 AI가 분석한 영어 이름과 비교할 이름
    private DogApiImageDto image; // 👈 이미지 객체 (위에서 만든 DTO)

    // JSON의 'life_span' 키를 Java의 'lifeSpan' 필드에 매핑
    @JsonProperty("life_span")
    private String lifeSpan;

    private String temperament;

    @JsonProperty("reference_image_id")
    private String referenceImageId;
}