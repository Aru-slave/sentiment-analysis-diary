package com.sentimentdiary.demo.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class DiaryDto {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Post {
        @NotBlank(message = "제목을 반드시 입력해주세요.")
        private String title;
        @NotBlank(message = "내용을 입력해주세요.")
        private String content;
    }

    @Getter
    public static class Patch {
        private long diaryId;
        private String title;
        private String content;
    }

    @NoArgsConstructor
    @Setter
    @Getter
    @AllArgsConstructor
    public static class Response {
        private long diaryId;
        private long memberId;
        private String nickName;
        private String title;
        private String content;
        private LocalDateTime createDate;
        private double sentimentScore;
    }
}
