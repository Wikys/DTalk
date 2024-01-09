package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;
//프로필 변경 받는데이터

public class editProfileResponse {

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
