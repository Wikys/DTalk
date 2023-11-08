package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//휴대폰 인증번호 확인 반환 메시지

public class certificationCheckResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("code") //응답값에 따라 상태 변경위해 만듬
    private int code;




    public String getMessage() {
        return message;
    }
    public int getCode() {
        return code;
    }

}
