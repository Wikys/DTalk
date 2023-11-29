package com.example.dtalk.retrofit;
//친구추가시 받을 정보

import com.google.gson.annotations.SerializedName;

public class addFriendResponse {


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
