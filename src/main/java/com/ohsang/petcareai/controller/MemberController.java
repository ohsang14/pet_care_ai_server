package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.domain.Member;
import com.ohsang.petcareai.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

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
}
