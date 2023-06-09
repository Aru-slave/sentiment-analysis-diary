package com.sentimentdiary.demo.diary;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class DiaryDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @ApiModel("Diary Post")
    public static class Post {
        @NotBlank(message = "제목을 반드시 입력해주세요.")
        @ApiModelProperty(notes = "제목", required = true, example = "오늘의 일기")
        private String title;

        @NotBlank(message = "내용을 입력해주세요.")
        @ApiModelProperty(notes = "내용", required = true, example = "저녁에 맛있는 치킨을 먹어서 기운이 났다.")
        private String content;

        @NotBlank(message = "날짜 입력해주세요.")
        @ApiModelProperty(notes = "날짜", required = true, example = "2023-04-26")
        private String createdAt;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("Diary Patch")
    public static class Patch {
        @ApiModelProperty(notes = "다이어리 식별자", required = true, example = "1")
        private long diaryId;

        @ApiModelProperty(notes = "제목", required = true, example = "오늘의 일기")
        private String title;

        @ApiModelProperty(notes = "내용", required = true, example = "저녁에 맛있는 치킨을 먹어서 기운이 났다.")
        private String content;
    }

    @NoArgsConstructor
    @Setter
    @Getter
    @AllArgsConstructor
    @ApiModel("Diary Response")
    public static class Response {
        @ApiModelProperty(notes = "다이어리 식별자", required = true, example = "1")
        private long diaryId;

        @ApiModelProperty(notes = "멤버 식별자", required = true, example = "1")
        private long memberId;

        @ApiModelProperty(notes = "이름", required = true, example = "홍길동")
        private String nickName;

        @ApiModelProperty(notes = "제목", required = true, example = "오늘의 일기")
        private String title;

        @ApiModelProperty(notes = "내용", required = true, example = "저녁에 맛있는 치킨을 먹어서 기운이 났다.")
        private String content;

        @ApiModelProperty(notes = "감정", required = true, example = "9")
        private int emotion;

        @ApiModelProperty(notes = "키워드", required = true, example = "{저녁, 1}")
        private List<String> keywords = new LinkedList<>();

        @ApiModelProperty(notes = "생성 날짜", required = true, example = "2023-01-09T18:00:00")
        private LocalDate createdAt;

        @ApiModelProperty(notes = "수정 날짜", required = true, example = "2023-01-09T18:00:00")
        private LocalDate modifiedAt;
    }
    @NoArgsConstructor
    @Setter
    @Getter
    @AllArgsConstructor
    @ApiModel("Diary analysis Response")
    public static class diaryAnalysis {
        @ApiModelProperty(notes = "다이어리 식별자", required = true, example = "1")
        private long diaryId;

        @ApiModelProperty(notes = "감정", required = true, example = "9")
        private int emotion;

        @ApiModelProperty(notes = "생성 날짜", required = true, example = "2023-01-09T18:00:00")
        private LocalDate createdAt;

    }
}
