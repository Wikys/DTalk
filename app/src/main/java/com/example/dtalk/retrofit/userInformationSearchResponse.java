package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;
//계정정보 불러오기 받는데이터

public class userInformationSearchResponse {
    @SerializedName("userNick")
    private String userNick;
    @SerializedName("userStatusMsg")
    private String userStatusMsg;
    @SerializedName("userProfileImg")
    private String userProfileImg;
    @SerializedName("userProfileBackImg")
    private String userProfileBackImg;

    @SerializedName("message")
    private String message;

    public String getUserNick() {
        return userNick;
    }

    public String getUserStatusMsg() {
        return userStatusMsg;
    }

    public String getUserProfileImg() {
        return userProfileImg;
    }

    public String getUserProfileBackImg() {
        return userProfileBackImg;
    }

    public String getMessage() {
        return message;
    }
}
