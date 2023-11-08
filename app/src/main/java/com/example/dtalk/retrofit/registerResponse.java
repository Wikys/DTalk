package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//회원가입 완료시 받을정보

public class registerResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private int code;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
