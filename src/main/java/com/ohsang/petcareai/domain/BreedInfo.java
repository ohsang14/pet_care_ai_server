package com.ohsang.petcareai.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "breed_info") // MySQL의 'breed_info' 테이블과 연결
@Getter
@Setter
public class BreedInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "breed_id")
    private Long id;

    @Column(name = "breed_name_en", nullable = false, unique = true)
    private String breedNameEn; // AI가 반환하는 영어 이름 (e.g., 'Maltese_dog')

    @Column(name = "breed_name_ko", nullable = false)
    private String breedNameKo; // "말티즈"

    @Column(name = "image_url", length = 2048) // URL은 길 수 있으므로
    private String imageUrl;
}