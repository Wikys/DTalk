package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//구글 로그인 서버 응답 데이터

public class GLoginResponse {

    @SerializedName("message")
    private String message;



    public String getMessage() {
        return message;
    }

}
