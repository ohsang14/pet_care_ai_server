package com.ohsang.petcareai.repository;

import com.ohsang.petcareai.domain.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {

    // 특정 강아지(dogId)의 건강 체크 기록을 최신순(checkDate 내림차순)으로 정렬
    List<HealthCheck> findByDogIdOrderByCheckDateDesc(Long dogId);
}