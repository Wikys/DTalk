package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//SMS 인증번호 요청 반환메시지

public class SMSVerifiResponse {

    @SerializedName("message")
    private String message;



    public String getMessage() {
        return message;
    }

}
