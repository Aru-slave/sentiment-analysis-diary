package com.sentimentdiary.demo.diary;

import com.sentimentdiary.demo.member.MemberController;
import com.sentimentdiary.demo.util.JwtMockBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class DiaryControllerTest extends JwtMockBean {
    private static DiaryDto.Response response;
    private static List<DiaryDto.Response> responses;
    @MockBean
    private DiaryService diaryService;

    @MockBean
    private DiaryMapper diaryMapper;

    @BeforeAll
    public static void initAll() {
        List<String> keywords = new ArrayList<>();
        response = new DiaryDto.Response();
        response.setDiaryId(1);
        response.setMemberId(1);
        response.setNickName("사용자1");
        response.setTitle("제목");
        response.setContent("내용");
        response.setEmotion(-2);
        response.setKeywords(keywords);
        response.setCreatedAt(LocalDate.parse("2023-04-20"));
        response.setModifiedAt(LocalDate.now());

        responses = new ArrayList<>();
        responses.add(response);

        startWithUrl = "/api/diary";
    }

    @Test
//    @WithMockUser
    @DisplayName("다이어리 생성")
    public void postDiaryTest() throws Exception {
        // given
        DiaryDto.Post post = new DiaryDto.Post(
                "제목",
                "내용",
                "2023-04-20"
        );

        given(diaryMapper.diaryPostDtoToDiary(Mockito.any(DiaryDto.Post.class))).willReturn(new Diary());
        given((diaryService.createDiary(Mockito.any(DiaryDto.Post.class)))).willReturn(new Diary());
        given(diaryMapper.diaryToDiaryResponseDto(Mockito.any(Diary.class))).willReturn(response);

        // when
        ResultActions actions =
                mockMvc.perform(
                        post(startWithUrl)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(post))
                                .header("Authorization", "Bear abc")
                                .characterEncoding("utf-8")
                );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()));
    }

    @Test
    @WithMockUser
    @DisplayName("다이어리 단일조회")
    public void getDiaryTest() throws Exception {
        // given
        long diaryId = 1;
        given(diaryService.findDiary(diaryId)).willReturn(new Diary());
        given(diaryMapper.diaryToDiaryResponseDto(Mockito.any(Diary.class))).willReturn(response);

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(startWithUrl + "/{diary-id}", diaryId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.diaryId").value(diaryId));
    }

    @Test
    @WithMockUser
    @DisplayName("날짜 및 멤버별 다이어리 단일 조회")
    public void getDiariesByDateTest() throws Exception {
        // given
        LocalDate createdAt = LocalDate.now();
        given(diaryService.findDiary(createdAt)).willReturn(new Diary());
        given(diaryMapper.diariesToStudyResponseDto(Mockito.anyList())).willReturn(responses);

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(startWithUrl + "/date")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("createdAt", createdAt.toString())
                );

        // then
        actions.andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    @DisplayName("멤버별 다이어리 부분 조회")
    public void getDiariesTest() throws Exception {
        // given
        int page = 1;
        int size = 1;
        given(diaryService.findDiaries(Mockito.anyInt(), Mockito.anyInt())).willReturn(Page.empty());
        given(diaryMapper.diariesToStudyResponseDto(Mockito.anyList())).willReturn(responses);

        // when
        ResultActions actions =
                mockMvc.perform(
                        get(startWithUrl)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", Integer.toString(page))
                                .param("size", Integer.toString(size))
                );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo.page").value(page));
    }

    @Test
    @WithMockUser
    @DisplayName("다이어리 수정")
    public void patchDiaryTest() throws Exception {
        // given
        int diaryId = 1;
        DiaryDto.Patch patch = new DiaryDto.Patch(
                1,
                "제목",
                "내용"
        );
        given(diaryMapper.diaryPatchDtoToDiary(Mockito.any(DiaryDto.Patch.class))).willReturn(new Diary());
        given(diaryService.updateDiary(Mockito.any(Diary.class))).willReturn(new Diary());
        given(diaryMapper.diaryToDiaryResponseDto(Mockito.any(Diary.class))).willReturn(response);

        // when
        ResultActions actions =
                mockMvc.perform(
                        patch(startWithUrl + "/{diary-id}", diaryId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(patch))
                );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.diaryId").value(diaryId))
                .andExpect(jsonPath("$.title").value(patch.getTitle()))
                .andExpect(jsonPath("$.content").value(patch.getContent()));
    }

    @Test
    @WithMockUser
    @DisplayName("다이어리 삭제")
    public void deleteDiaryTest() throws Exception {
        // given
        int diaryId = 1;

        // when
        ResultActions actions =
                mockMvc.perform(
                        delete(startWithUrl + "/{diary-id}", diaryId)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        // then
        actions.andExpect(status().isNoContent());
    }
}
