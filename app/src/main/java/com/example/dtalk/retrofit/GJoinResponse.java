package com.example.dtalk.retrofit;

//구글 회원가입 서버 응답 데이터

import com.google.gson.annotations.SerializedName;

public class GJoinResponse {

    @SerializedName("message")
    private String message;


    public String getMessage() {
        return message;
    }
}
