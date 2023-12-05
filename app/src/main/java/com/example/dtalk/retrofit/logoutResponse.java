package com.example.dtalk.retrofit;

//로그아웃 시키고 받을 데이터

import com.google.gson.annotations.SerializedName;

public class logoutResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

}
