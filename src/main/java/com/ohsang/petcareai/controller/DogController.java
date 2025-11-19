package com.ohsang.petcareai.controller;

import com.ohsang.petcareai.domain.Dog;
import com.ohsang.petcareai.domain.Member;
import com.ohsang.petcareai.dto.DogResponseDto; // 👈 DTO를 import 합니다.
import com.ohsang.petcareai.repository.DogRepository;
import com.ohsang.petcareai.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // 👈 리스트 변환을 위해 import 합니다.

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DogController {

    private final DogRepository dogRepository;
    private final MemberRepository memberRepository;

    /**
     * 특정 회원의 반려견 등록 API
     * [수정] 반환 타입을 Dog에서 DogResponseDto로 변경
     */
    @PostMapping("/members/{memberId}/dogs")
    public ResponseEntity<DogResponseDto> addDog(
            @PathVariable Long memberId,
            @RequestBody Dog dog) {

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Member owner = optionalMember.get();
        dog.setMember(owner);
        Dog savedDog = dogRepository.save(dog);

        // Entity(savedDog)를 DTO로 변환해서 반환합니다.
        return ResponseEntity.status(HttpStatus.CREATED).body(new DogResponseDto(savedDog));
    }

    /**
     * 특정 회원의 반려견 목록 조회 API
     * [수정] 반환 타입을 List<Dog>에서 List<DogResponseDto>로 변경
     */
    @GetMapping("/members/{memberId}/dogs")
    public ResponseEntity<List<DogResponseDto>> getDogsByMember(@PathVariable Long memberId) {

        if (!memberRepository.existsById(memberId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 1. DB에서 Entity 리스트를 조회합니다.
        List<Dog> dogs = dogRepository.findByMemberId(memberId);

        // 2. Entity 리스트를 DTO 리스트로 변환합니다. (Java Stream 사용)
        List<DogResponseDto> dogDtos = dogs.stream()
                .map(DogResponseDto::new) // .map(dog -> new DogResponseDto(dog))와 동일
                .collect(Collectors.toList());

        // 3. DTO 리스트를 클라이언트에게 반환합니다.
        return ResponseEntity.ok(dogDtos);
    }

    @DeleteMapping("/dogs/{dogId}")
    public ResponseEntity<Void> deleteDog(@PathVariable Long dogId) {

        // 1. 삭제할 반려견이 존재하는지 확인
        if (!dogRepository.existsById(dogId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            // 2. ID를 기준으로 반려견 삭제
            dogRepository.deleteById(dogId);

            // 3. 성공 시 200 OK 또는 204 No Content 반환
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/dogs/{dogId}")
    public ResponseEntity<DogResponseDto> updateDog(
            @PathVariable Long dogId,
            @RequestBody Dog updatedDogData) { // 1. Flutter에서 보낸 수정된 정보

        // 2. 수정할 반려견이 DB에 존재하는지 확인
        Optional<Dog> optionalDog = dogRepository.findById(dogId);
        if (optionalDog.isEmpty()) {
            // 존재하지 않는 dogId이면 404 Not Found 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Dog existingDog = optionalDog.get();

        // 3. DTO(updatedDogData)의 정보로 existingDog 엔티티의 필드를 덮어쓰기
        existingDog.setName(updatedDogData.getName());
        existingDog.setBirthDate(updatedDogData.getBirthDate());
        existingDog.setProfileImageUrl(updatedDogData.getProfileImageUrl());
        existingDog.setBreed(updatedDogData.getBreed());
        existingDog.setGender(updatedDogData.getGender());
        existingDog.setIsNeutered(updatedDogData.getIsNeutered());
        existingDog.setWeight(updatedDogData.getWeight());


        try {
            // 4. DB에 저장 (JPA가 변경된 내용을 감지하고 update 쿼리 실행)
            Dog savedDog = dogRepository.save(existingDog);

            // 5. 수정된 결과를 DTO로 변환하여 200 OK와 함께 반환
            return ResponseEntity.ok(new DogResponseDto(savedDog));

        } catch (Exception e) {
            e.printStackTrace();
            // 저장 중 에러 발생 시 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
