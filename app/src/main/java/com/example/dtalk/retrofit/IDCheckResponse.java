package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//아이디 중복체크 반환메시지

public class IDCheckResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("code") //응답값에 따라 다이얼로그 상태 변경위해 만듬
    private int code;



    public String getMessage() {
        return message;
    }
    public int getCode() {
        return code;
    }
}
