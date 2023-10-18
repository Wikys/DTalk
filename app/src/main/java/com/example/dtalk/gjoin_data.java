package com.example.dtalk;

import com.google.gson.annotations.SerializedName;

public class gjoin_data {
    @SerializedName("userName")
    private String userName;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userPwd")
    private String userPwd;

    public gjoin_data(String userName, String userEmail, String userPwd) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPwd = userPwd;
    }
}
