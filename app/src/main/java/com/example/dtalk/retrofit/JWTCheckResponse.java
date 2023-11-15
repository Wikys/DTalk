package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//JWT 체크하고 받을정보

public class JWTCheckResponse {

    @SerializedName("status") //상태
    private String status;

    @SerializedName("message")
    private String message;
    @SerializedName("userId")
    private String userId;


    @SerializedName("accessToken")
    private String accessToken;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
