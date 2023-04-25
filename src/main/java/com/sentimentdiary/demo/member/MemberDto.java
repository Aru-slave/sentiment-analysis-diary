package com.sentimentdiary.demo.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class MemberDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("Member Post")
    public static class Post{
        @Email
        @NotBlank(message = "이메일을 입력해주세요")
        @ApiModelProperty(notes = "이메일", required = true, example = "test@gmail.com")
        private String email;

        @NotBlank(message = "패스워드를 입력해주세요.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,}", message = "비밀번호는 8자 이상, 영문 소문자, 숫자, 특수문자를 적어도 1개 포함시켜주세요")
        @ApiModelProperty(notes = "비밀번호", required = true, example = "qwer1234!!")
        private String pw;
        @NotBlank(message = "패스워드를 입력해주세요.")
        @ApiModelProperty(notes = "닉네임", required = true, example = "홍길동")
        private String nickName;
    }

    @Getter
    @Setter
    @ApiModel("Member Patch")
    public static class Patch{
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,}", message = "비밀번호는 8자 이상, 영문 소문자, 숫자, 특수문자를 적어도 1개 포함시켜주세요")
        @ApiModelProperty(notes = "비밀번호", required = true, example = "qwer1234!!")
        private String pw;

        @NotBlank(message = "별명을 입력해주세요.")
        @ApiModelProperty(notes = "이름", required = true, example = "홍길동")
        private String nickName;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel("Member Check")
    public static class Check {
        @Email
        @ApiModelProperty(notes = "이메일", required = true, example = "test@gmail.com")
        private String email;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel("Member Response")
    public static class Response {
        @ApiModelProperty(notes = "멤버 식별자", required = true, example = "1")
        private Long memberId;
        @ApiModelProperty(notes = "이름", required = true, example = "홍길동")
        private String nickName;
        @ApiModelProperty(notes = "이메일", required = true, example = "test@gmail.com")
        private String email;
        @ApiModelProperty(notes = "구글 로그인 여부", required = true, example = "1")
        private boolean google;
    }
}