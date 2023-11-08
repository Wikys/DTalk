package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//회원가입시 보낼정보 DTO

public class registerData {

    @SerializedName("userId")
    private String userId;

    @SerializedName("userPwd")
    private String userPwd;

    @SerializedName("userNick")
    private String userNick;

    @SerializedName("phone_num")
    String phone_num;

    public registerData(String userId, String userPwd, String userNick, String phone_num) {
        this.userId = userId;
        this.userPwd = userPwd;
        this.userNick = userNick;
        this.phone_num = phone_num;
    }
}
