package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//로그인 서버로 보낼 데이터

public class loginData {

    @SerializedName("userId")
    String userId;
    @SerializedName("userPwd")
    String userPwd;

    public loginData(String userId, String userPwd) {
        this.userId = userId;
        this.userPwd = userPwd;
    }
}
