package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;
//아이디찾기 받는데이터

public class findIdResultResponse {

    @SerializedName("ID")
    private String ID;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public String getID() {
        return ID;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
