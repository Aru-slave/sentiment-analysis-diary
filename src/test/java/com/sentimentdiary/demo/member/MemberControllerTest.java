package com.sentimentdiary.demo.member;

import com.google.gson.Gson;
import com.sentimentdiary.demo.auth.dto.LoginResponseDto;
import com.sentimentdiary.demo.util.JwtMockBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class MemberControllerTest extends JwtMockBean {
    private static MemberDto.Response response;
    private static List<MemberDto.Response> responses;
    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper memberMapper;

    @Autowired
    private Gson gson;

    @BeforeAll
    public static void initAll() {
        response = new MemberDto.Response();
        response.setMemberId(1L);
        response.setNickName("사용자");
        response.setEmail("test@gmail.com");
        response.setGoogle(true);

        responses = new ArrayList<>();
        responses.add(response);

        startWithUrl = "/api/members";
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입")
    public void postMemberTest() throws Exception {
        // given
        MemberDto.Post post = new MemberDto.Post(
                "test@gmail.com",
                "1234ASdf!!",
                "테스트"
        );

        given(memberMapper.memberPostToMember(Mockito.any(MemberDto.Post.class))).willReturn(new Member());
        given(memberService.createMember(Mockito.any(Member.class))).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        // when
        ResultActions actions =
                mockMvc.perform(
                        post(startWithUrl)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(post))
                                .characterEncoding("utf-8")
                );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(post.getEmail()));
    }

    @Test
    @WithMockUser
    @DisplayName("이메일 또는 이름 존재 확인")
    public void checkDetailTest() throws Exception {
        // given
        MemberDto.Check check = new MemberDto.Check();
        check.setEmail("test@gmail.com");

        given(memberService.check(Mockito.any(MemberDto.Check.class))).willReturn(true);

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(startWithUrl + "/check")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(gson.toJson(check))
                );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(response.isGoogle()));
    }

    @Test
    @WithMockUser
    @DisplayName("최초 구글 로그인 시 이름 변경")
    public void patchGoogleMemberTest() throws Exception {
        // given
        MemberDto.Patch patch = new MemberDto.Patch();
        patch.setPw("qewr1234~~");
        patch.setNickName("사용자");

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setMemberId(1L);
        loginResponseDto.setNickName("사용자");

        given(memberMapper.memberPatchToMember(patch)).willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());
        given(memberMapper.googleMemberToGoogleMemberResponse(Mockito.any(Member.class))).willReturn(loginResponseDto);

        // when
        ResultActions actions =
                mockMvc.perform(
                        patch(startWithUrl + "/patchGoogleMember")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(gson.toJson(patch))
                );

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("최초 구글 로그인이 아닐 때, 필요정보 반환")
    public void getGoogleMemberTest() throws Exception {
        // given
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setMemberId(1L);
        loginResponseDto.setNickName("사용자");

        given(memberService.getLoginMember()).willReturn(new Member());
        given(memberMapper.googleMemberToGoogleMemberResponse(Mockito.any(Member.class))).willReturn(loginResponseDto);

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(startWithUrl + "/getGoogleMember")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickName").value(loginResponseDto.getNickName()));
    }

    @Test
    @WithMockUser
    @DisplayName("전체 멤버 조회")
    public void getMembersTest() throws Exception {
        // given
        given(memberService.findMembers()).willReturn(new ArrayList<>());
        given(memberMapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(startWithUrl + "/getGoogleMember")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                );

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("로그인")
    public void getLoginTest() throws Exception {
        // given
        given(memberService.findMembers()).willReturn(new ArrayList<>());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(startWithUrl + "/login")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                );

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("로그인")
    public void searchTest() throws Exception {
        // given
        String nickname = "사용자";
        given(memberService.findByNiceName(Mockito.anyString())).willReturn(new Member());
        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(startWithUrl + "/search")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .param("nickname", nickname)
                );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickName").value(nickname));
    }

    @Test
    @WithMockUser
    @DisplayName("멤버 삭제")
    public void deleteMemberTest() throws Exception {
        // given
        long memberId = 1L;

        // when
        ResultActions actions =
                mockMvc.perform(
                        delete(startWithUrl + "/{member-id}", memberId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                );

        // then
        actions.andExpect(status().isNoContent());
    }
}
