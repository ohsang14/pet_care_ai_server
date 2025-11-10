package com.ohsang.petcareai.repository;

import com.ohsang.petcareai.domain.BreedInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BreedInfoRepository extends JpaRepository<BreedInfo, Long> {

    // (이 부분이 이번 업그레이드의 핵심입니다!)
    //
    // Spring Data JPA가 메소드 이름을 분석해서
    // 'breed_name_en' 컬럼을 기준으로 데이터를 찾는 쿼리를
    // 자동으로 생성해줍니다. (e.g., SELECT * FROM breed_info WHERE breed_name_en = ?)
    Optional<BreedInfo> findByBreedNameEn(String breedNameEn);
}