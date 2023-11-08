package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//SMS 인증번호 요청 반환메시지,코드

public class SMSVerifiResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("count")
    private String count;

    @SerializedName("code")
    private String code;


    public String getMessage() {
        return message;
    }
    public String getCode() {
        return code;
    }
    public String getCount() {
        return count;
    }


}
