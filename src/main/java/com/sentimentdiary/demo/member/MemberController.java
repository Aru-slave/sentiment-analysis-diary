package com.sentimentdiary.demo.member;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("api/members")
@Validated
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper mapper;



    //회원가입
    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberDto.Post requestBody) {
        Member member = memberService.createMember(mapper.memberPostToMember(requestBody));

        return ResponseEntity.ok(mapper.memberToMemberResponse(member));
    }
    @PostMapping("/check")
    public ResponseEntity checkDetail(@RequestBody MemberDto.Check requestBody){
        return ResponseEntity.ok(memberService.check(requestBody));
    }

    //최초 구글 로그인 시 폰번호, 닉네임 기입 후 필요 정보 반환
    @PatchMapping("/patchGoogleMember")
    public ResponseEntity patchGoogleMember(@Valid @RequestBody MemberDto.Patch requestBody) {
        Member member = memberService.updateMember(mapper.memberPatchToMember(requestBody));

        return ResponseEntity.ok(mapper.googleMemberToGoogleMemberResponse(member));
    }

    //최초 구글 로그인 x -> 필요 정보 반환
    @GetMapping("/getGoogleMember")
    public ResponseEntity getGoogleMember() {
        Member member = memberService.getLoginMember();
        return ResponseEntity.ok(mapper.googleMemberToGoogleMemberResponse(member));
    }


    //등록된 회원 전체 가져오기
    @GetMapping
    public ResponseEntity getMembers() {
        return ResponseEntity.ok(mapper.membersToMemberResponses(memberService.findMembers()));
    }

    //로그인시 api
    @GetMapping("/login")
    public ResponseEntity getLogin() {
        return ResponseEntity.ok(mapper.memberToMemberResponse(memberService.getLoginMember()));
    }

    //회원정보 가져오기
    @GetMapping("/myInformation")
    public ResponseEntity getMember() {
        return ResponseEntity.ok(mapper.memberToMemberResponse(memberService.getLoginMember()));
    }

    //회원탈퇴
    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(
            @PathVariable("member-id") @Positive long memberId) {
        memberService.deleteMember(memberId);

        return ResponseEntity.ok().build();
    }

    //닉네임으로 회원 검색
    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(value = "keyword") String keyword) {
        return ResponseEntity.ok(mapper.memberToMemberResponse(memberService.findByNiceName(keyword)));
    }



}