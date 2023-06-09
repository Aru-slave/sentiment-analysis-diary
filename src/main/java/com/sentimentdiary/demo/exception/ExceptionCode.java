package com.sentimentdiary.demo.exception;

import lombok.Getter;

public enum ExceptionCode {
     MEMBER_NOT_FOUND(400, "Member not found"),
    MEMBER_EXISTS(401, "Member exists"),
    NOT_LOGIN(402,"login, please"),
    BOARD_NOT_FOUND(403,"not found board"),
    PERMISSION_DENIED(404,"check, please"),
    FULL_MEMBER(405,"Full member"),

    LOCATION_NOT_FOUND(407, "Location not found"),

    DIARY_IS_EXIST(408,"Diary is exist"),
    BAN(415, "Bye"),
    DIARY_NOT_FOUND(416,"Diary not found"),
    NICKNAME_EXIST(417,"NickName is exists"),
    PHONE_NUMBER_EXIST(418,"phoneNumber is exists"),
    EMAIL_EXIST(419,"email is exists"),
    REPORT_TYPE_NOT_FOUND(420, "ReportType not found"),
    TIME_OUT(421,"Time Out!"),
    JWT_EXPIRE(422,"Jwt expired"),
    NOT_VALIDATE(423, "Not validate token"),
    REFRESH_TOKEN_NOT_FOUND(424, "RefreshToken not found"),
    RECRUITED_COMPLETE(425," Recruitment Complete"),
    NOT_CHATROOM_MASTER(426,"Not chatroom master");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message){
        this.status = code;
        this.message = message;
    }
}
