package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//구글 로그인 보낼 데이터

public class GLoginData {

    @SerializedName("userId")
    String userId;

    @SerializedName("userPwd")
    String userPwd;

    @SerializedName("userNick")
    String userNick;

    public GLoginData(String userId, String userPwd, String userNick) {
        this.userId = userId;
        this.userPwd = userPwd;
        this.userNick = userNick;
    }
}
