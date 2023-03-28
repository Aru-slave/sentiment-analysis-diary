package com.sentimentdiary.demo.member;

import com.sentimentdiary.demo.auth.dto.LoginResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    Member memberPostToMember(MemberDto.Post requestBody);
    Member memberPatchToMember(MemberDto.Patch requestBody);
    MemberDto.Response memberToMemberResponse(Member member);
    default LoginResponseDto googleMemberToGoogleMemberResponse(Member member){
        LoginResponseDto dto = new LoginResponseDto();
        dto.setMemberId(member.getMemberId());
        dto.setNickName(member.getNickName());
        dto.setRole(member.getRoles().get(0));
        return dto;
    }
    List<MemberDto.Response> membersToMemberResponses(List<Member> members);
}