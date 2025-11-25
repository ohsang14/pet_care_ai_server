package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.domain.HealthCheck;
import com.ohsang.petcareai.domain.Member;
import com.ohsang.petcareai.dto.HealthCheckResponseDto;
import com.ohsang.petcareai.dto.KakaoLoginDto;
import com.ohsang.petcareai.repository.HealthCheckRepository;
import com.ohsang.petcareai.repository.MemberRepository;
import com.ohsang.petcareai.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final HealthCheckRepository healthCheckRepository;
    private final KakaoService kakaoService;

    /**
     * 일반 회원가입
     */
    @PostMapping("/join")
    public Member join(@RequestBody Member member) {
        return memberRepository.save(member);
    }

    /**
     * 일반 이메일 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Member loginRequest) {
        Optional<Member> optionalMember = memberRepository.findByEmail(loginRequest.getEmail());

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (member.getPassword().equals(loginRequest.getPassword())) {
                // 보안상 비밀번호는 null 처리하여 반환하지 않음 (선택사항)
                // member.setPassword(null);
                return ResponseEntity.ok(member);
            }
        }
        return ResponseEntity.status(401).body("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    /**
     * [업그레이드 완료] 카카오 로그인
     * 프론트에서 Access Token을 받아 카카오 서비스에 위임 -> 회원 객체 반환
     */
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoLoginDto kakaoDto) {
        try {
            // 1. 서비스에 토큰 전달 -> 사용자 정보 조회 -> DB 조회/가입 -> Member 반환
            Member member = kakaoService.kakaoLogin(kakaoDto.getAccessToken());

            // 2. 로그인 성공한 회원 정보 반환
            return ResponseEntity.ok(member);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("카카오 로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 회원 정보 수정
     */
    @PutMapping("/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable Long memberId, @RequestBody Member updateData) {
        return memberRepository.findById(memberId)
                .map(member -> {
                    if (updateData.getName() != null) member.setName(updateData.getName());
                    if (updateData.getPassword() != null) member.setPassword(updateData.getPassword());
                    if (updateData.getProfileImageUrl() != null)
                        member.setProfileImageUrl(updateData.getProfileImageUrl());
                    if (updateData.getPhoneNumber() != null) member.setPhoneNumber(updateData.getPhoneNumber());
                    if (updateData.getAddress() != null) member.setAddress(updateData.getAddress());

                    Member saved = memberRepository.save(member);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 회원의 건강 기록 전체 조회 (마이페이지용)
     */
    @GetMapping("/{memberId}/health-checks")
    public ResponseEntity<List<HealthCheckResponseDto>> getMemberHealthChecks(@PathVariable Long memberId) {
        // 1. 리포지토리에서 회원 ID로 모든 기록 조회
        List<HealthCheck> checks = healthCheckRepository.findByDogMemberIdOrderByCheckDateDesc(memberId);

        // 2. DTO로 변환
        List<HealthCheckResponseDto> dtoList = checks.stream()
                .map(HealthCheckResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            memberRepository.deleteById(memberId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}