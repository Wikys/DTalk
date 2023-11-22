package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//비밀번호 찾기 서버로 보낼 데이터
public class findPsResultData {

    @SerializedName("userId")
    String userId;
    @SerializedName("userPs")
    String userPs;

    public findPsResultData(String userId, String userPs) {
        this.userId = userId;
        this.userPs = userPs;
    }
}
