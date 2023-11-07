package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//ID체크 서버로 보낼 데이터

public class IDCheckData {

    @SerializedName("userID")
    String userID;

    public IDCheckData(String userID) {
        this.userID = userID;
    }

}
