package com.ohsang.petcareai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cat_breed_info")
@Getter
@Setter
public class CatBreedInfo {

    @Id
    @Column(name = "breed_name_en")
    private String breedNameEn;

    @Column(name = "breed_name_ko", nullable = false)
    private String breedNameKo;

    @Column(name = "api_search_term") // 👈 이 필드를 추가
    private String apiSearchTerm;
}