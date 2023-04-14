package com.sentimentdiary.demo.member;


import com.sentimentdiary.demo.auth.dto.LoginResponseDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("api/members")
@Validated
@RequiredArgsConstructor
@ApiOperation(value = "회원 정보 API", tags = {"Member-Controller"}) // Swagger
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper mapper;

    //회원가입
    @PostMapping
    @ApiOperation(value = "회원가입", notes = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "회원가입 완료")
    })
    public ResponseEntity<MemberDto.Response> postMember(@Valid @RequestBody MemberDto.Post requestBody) {
        Member member = memberService.createMember(mapper.memberPostToMember(requestBody));
        MemberDto.Response response = mapper.memberToMemberResponse(member);

        return new ResponseEntity<MemberDto.Response>(response, HttpStatus.CREATED);

    }

    // 이메일이나 닉네임이 이미 존재하는지 확인
    @GetMapping("/check")
    @ApiOperation(value = "이메일 또는 이름 존재 확인", notes = "email 또는 nickname이 이미 존재하는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "사용하지 않음"),
            @ApiResponse(code = 417, message = "NickName is exists")
    })
    public ResponseEntity<Boolean> checkDetail(@RequestBody MemberDto.Check requestBody){
        return new ResponseEntity<Boolean>(memberService.check(requestBody), HttpStatus.OK);
    }

    // 최초 구글 로그인 시 닉네임 기입 후 필요 정보 반환
    @PatchMapping("/patchGoogleMember")
    @ApiOperation(value = "최초 구글 로그인 시 이름 변경", notes = "최초 구글 로그인 시 이름를 변경할 수 있다.")
    public ResponseEntity<LoginResponseDto> patchGoogleMember(@Valid @RequestBody MemberDto.Patch requestBody) {
        Member member = memberService.updateMember(mapper.memberPatchToMember(requestBody));

        return ResponseEntity.ok(mapper.googleMemberToGoogleMemberResponse(member));
    }

    //최초 구글 로그인 x -> 필요 정보 반환
    @GetMapping("/getGoogleMember")
    @ApiOperation(value = "최초 구글 로그인이 아닐 때, 필요정보 반환", notes = "최초 구글 로그인 아닐 때 필요한 정보를 반환한다.")
    public ResponseEntity<LoginResponseDto> getGoogleMember() {
        Member member = memberService.getLoginMember();
        return ResponseEntity.ok(mapper.googleMemberToGoogleMemberResponse(member));
    }


    //등록된 회원 전체 가져오기
    @GetMapping
    @ApiOperation(value = "전체 멤버 조회", notes = "모든 멤버 정보를 반환한다.")
    public ResponseEntity<List<MemberDto.Response>> getMembers() {
        return ResponseEntity.ok(mapper.membersToMemberResponses(memberService.findMembers()));
    }

    //로그인시 api
    @GetMapping("/login")
    @ApiOperation(value = "로그인", notes = "로그인한 멤버 정보를 반환한다.")
    public ResponseEntity<MemberDto.Response> getLogin() {
        return ResponseEntity.ok(mapper.memberToMemberResponse(memberService.getLoginMember()));
    }

//    //회원정보 가져오기
//    @GetMapping("/myInformation")
//    public ResponseEntity getMember() {
//        return ResponseEntity.ok(mapper.memberToMemberResponse(memberService.getLoginMember()));
//    }

    //회원탈퇴
    @DeleteMapping("/{member-id}")
    @ApiOperation(value = "멤버 삭제", notes = "nickname을 통해서 멤버를 검색합니다.")
    public ResponseEntity deleteMember(
            @ApiParam(name = "member-id", value = "멤버 식별자", required = true, example = "1")
            @PathVariable("member-id") @Positive long memberId) {
        memberService.deleteMember(memberId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //닉네임으로 회원 검색
    @GetMapping("/search")
    @ApiOperation(value = "이름으로 멤버 검색", notes = "nickname을 통해서 멤버를 검색합니다.")
    public ResponseEntity<MemberDto.Response> search(
            @ApiParam(name = "nickname", value = "이름", required = true, example = "홍길동")
            @RequestParam(value = "nickname") String nickname) {

        return new ResponseEntity(
                mapper.memberToMemberResponse(memberService.findByNiceName(nickname)),
                HttpStatus.OK
        );
    }

}