package com.ohsang.petcareai.repository;

import com.ohsang.petcareai.domain.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findByMemberId(Long memberId);
}