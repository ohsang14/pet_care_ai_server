package com.ohsang.petcareai.repository;

import com.ohsang.petcareai.domain.SymptomLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SymptomLogRepository extends JpaRepository<SymptomLog, Long> {

    // 특정 강아지(dogId)의 증상 기록을 찾되, 최신순(logDate 내림차순)으로 정렬
    List<SymptomLog> findByDogIdOrderByLogDateDesc(Long dogId);
}