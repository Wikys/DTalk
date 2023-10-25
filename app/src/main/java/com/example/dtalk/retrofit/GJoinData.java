package com.example.dtalk.retrofit;

//구글 회원가입 보낼 데이터

import com.google.gson.annotations.SerializedName;

public class GJoinData {


    @SerializedName("userId")
    private String userId;

    @SerializedName("userPwd")
    private String userPwd;

    @SerializedName("userNick")
    private String userNick;

    public GJoinData(String userId, String userPwd, String userNick) {
        this.userId = userId;
        this.userPwd = userPwd;
        this.userNick = userNick;
    }

}
