package com.example.dtalk.retrofit;

//로그인시 받을정보

import com.google.gson.annotations.SerializedName;

public class loginResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("JWT") //토큰
    private String JWT;

    public String getMessage() {
        return message;
    }

    public String getJWT() {
        return JWT;
    }

}
