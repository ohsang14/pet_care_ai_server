package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.domain.HealthCheck;
import com.ohsang.petcareai.domain.Member;
import com.ohsang.petcareai.dto.HealthCheckResponseDto; // 👈 DTO import
import com.ohsang.petcareai.repository.HealthCheckRepository; // 👈 리포지토리 import
import com.ohsang.petcareai.repository.MemberRepository;
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
    private final HealthCheckRepository healthCheckRepository; // 👈 1. 주입 추가

    @PostMapping("/join")
    public Member join(@RequestBody Member member) {
        return memberRepository.save(member);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Member loginRequest) {
        Optional<Member> optionalMember = memberRepository.findByEmail(loginRequest.getEmail());

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (member.getPassword().equals(loginRequest.getPassword())) {
                member.setPassword(null);
                return ResponseEntity.ok(member);
            }
        }
        return ResponseEntity.status(401).body("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable Long memberId, @RequestBody Member updateData) {
        return memberRepository.findById(memberId)
                .map(member -> {
                    if (updateData.getName() != null) member.setName(updateData.getName());
                    if (updateData.getPassword() != null) member.setPassword(updateData.getPassword());
                    Member saved = memberRepository.save(member);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * [이동됨] 특정 회원의 모든 반려견 건강 기록 조회 (마이페이지용)
     * URL: GET /api/members/{memberId}/health-checks
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
     * 회원 탈퇴 (DELETE)
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