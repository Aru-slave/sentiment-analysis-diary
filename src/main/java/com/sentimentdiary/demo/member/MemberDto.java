package com.sentimentdiary.demo.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MemberDto {
    @Getter
    @NoArgsConstructor
    public static class Post{

        @NotBlank(message = "패스워드를 입력해주세요.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,}", message = "비밀번호는 8자 이상, 영문 소문자, 숫자, 특수문자를 적어도 1개 포함시켜주세요")
        private String pw;
        @NotBlank(message = "별명을 입력해주세요.")
        private String nickName;
    }
    @Getter
    public static class Patch{


        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,}", message = "비밀번호는 8자 이상, 영문 소문자, 숫자, 특수문자를 적어도 1개 포함시켜주세요")
        private String pw;
        @NotBlank(message = "별명을 입력해주세요.")
        private String nickName;

    }
    @Getter
    @NoArgsConstructor
    public static class Check{
        @NotBlank(message = "별명을 입력해주세요.")
        private String nickName;
        @Email
        private String email;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private Long memberId;
        private String nickName;
        private String email;
        private boolean google;
    }
}