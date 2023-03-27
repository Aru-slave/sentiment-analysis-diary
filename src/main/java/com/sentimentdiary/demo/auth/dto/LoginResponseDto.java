package com.sentimentdiary.demo.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponseDto {
    private long memberId;
    private String nickName;
    private String role;
}