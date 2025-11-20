package com.ohsang.petcareai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "breed_info") // DB 테이블 이름과 매칭
@Getter
@Setter
public class BreedInfo {

    @Id // breed_name_en을 PK로 사용
    @Column(name = "breed_name_en")
    private String breedNameEn;

    @Column(name = "breed_name_ko", nullable = false)
    private String breedNameKo;

    @Column(name = "api_search_term")
    private String apiSearchTerm;
}