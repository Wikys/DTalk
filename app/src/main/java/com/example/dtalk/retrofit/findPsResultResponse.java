package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;
//비밀번호찾기 받는데이터

public class findPsResultResponse {

    @SerializedName("Ps")
    private String Ps;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public String getPs() {
        return Ps;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
