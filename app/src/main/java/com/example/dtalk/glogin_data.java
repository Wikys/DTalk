package com.example.dtalk;

import com.google.gson.annotations.SerializedName;

public class glogin_data {
    @SerializedName("userEmail")
    String userEmail;

    @SerializedName("nick")
    String nick;

    @SerializedName("userPwd")
    String userPwd;

    public glogin_data(String userEmail, String nick, String userPwd) {
        this.userEmail = userEmail;
        this.nick = nick;
        this.userPwd = userPwd;
    }
}
