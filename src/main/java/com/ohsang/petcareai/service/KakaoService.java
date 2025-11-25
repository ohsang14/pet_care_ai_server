package com.ohsang.petcareai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsang.petcareai.domain.Member;
import com.ohsang.petcareai.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final MemberRepository memberRepository;

    public Member kakaoLogin(String accessToken) throws JsonProcessingException {
        // 1. 카카오 서버로 사용자 정보 요청
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // "Bearer " 뒤에 공백 필수!
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        // 카카오 API 호출
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        // 2. 응답 JSON 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        Long kakaoId = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("kakao_account").get("profile").get("nickname").asText();

        // 이메일 처리 (동의 안 했을 경우 임시 이메일 생성)
        String email = "kakao_" + kakaoId + "@petcare.com";
        if (jsonNode.get("kakao_account").has("email")) {
            email = jsonNode.get("kakao_account").get("email").asText();
        }

        // ==========================================
        // 3. 회원 가입 여부 확인 및 처리 (핵심 로직)
        // ==========================================

        // 3-1. 카카오 ID로 이미 가입된 회원이 있는지 먼저 확인
        Member member = memberRepository.findByKakaoId(kakaoId).orElse(null);

        if (member == null) {
            // 3-2. 카카오 ID는 없지만, 혹시 '같은 이메일'을 쓰는 기존 회원이 있는지 확인 (계정 통합)
            Member sameEmailMember = memberRepository.findByEmail(email).orElse(null);

            if (sameEmailMember != null) {
                // [Case A] 기존 회원이 존재함 -> 카카오 ID만 추가하여 계정 연결(통합)
                member = sameEmailMember;
                member.setKakaoId(kakaoId);
                memberRepository.save(member); // 업데이트
            } else {
                // [Case B] 이메일도 없고 카카오 ID도 없음 -> 진짜 신규 가입
                member = new Member();
                member.setKakaoId(kakaoId);
                member.setName(nickname);
                member.setEmail(email);
                member.setPassword(UUID.randomUUID().toString()); // 비밀번호는 랜덤 처리

                memberRepository.save(member); // 신규 저장
            }
        }

        // 4. 로그인 성공한 회원 객체 반환
        return member;
    }
}