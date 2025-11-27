package com.ohsang.petcareai.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id // 고유 식별자
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name ="kakao_id", unique = true)
    private Long kakaoId;

    private String profileImageUrl;
    private String phoneNumber;
    private String address;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 무한루프 방지
    private List<Dog> dogs = new ArrayList<>();
}
