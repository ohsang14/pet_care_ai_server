package com.ohsang.petcareai.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity // 이 클래스가 데이터베이스 테이블과 매핑됨을 알려줌
@Getter
@Setter
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dog_id")
    private Long id;

    @Column(nullable = false)
    private String name; // 반려견 이름

    private LocalDate birthDate; // 반려견 생년월일

    // --- 가장 중요한 부분 ---
    // '다대일' 관계 설정
    // 여러 마리(Many)의 강아지는 한 명(One)의 회원에게 속한다.
    @ManyToOne
    // 실제 DB 테이블에 'member_id'라는 이름의 컬럼을 생성
    // 이 컬럼은 MEMBER 테이블의 PK와 연결되는 Foreign Key(외래 키)가 됨
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
