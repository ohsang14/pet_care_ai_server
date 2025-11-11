package com.ohsang.petcareai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CatApiResponseDto {

    private String id;
    private String name;

    // 1. CatApiImageDto image 필드 삭제
    // private CatApiImageDto image;

    @JsonProperty("life_span")
    private String lifeSpan;

    private String temperament;

    @JsonProperty("reference_image_id") // 2. API가 주는 참조 ID
    private String referenceImageId;

    // 3. 우리가 직접 채워 넣을 최종 이미지 URL 필드
    private String imageUrl;
}