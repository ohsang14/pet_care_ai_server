package com.ohsang.petcareai.repository;

import com.ohsang.petcareai.domain.CatBreedInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 1. CatBreedInfo 엔티티를 사용
// 2. PK 타입이 String 이므로 JpaRepository<CatBreedInfo, String>
public interface CatBreedInfoRepository extends JpaRepository<CatBreedInfo, String> {

    Optional<CatBreedInfo> findByBreedNameEn(String breedNameEn);
}