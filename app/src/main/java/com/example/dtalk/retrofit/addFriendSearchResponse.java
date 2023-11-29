package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;
//아이디 검색해서 친구추가할때 아이디목록 불러오는 클래스

public class addFriendSearchResponse {
    @SerializedName("userNick")
    private String userNick;

    @SerializedName("userStatusMsg")
    private String userStatusMsg;

    @SerializedName("userProfileImg")
    private String userProfileImg;

    @SerializedName("message")
    private String message;

    @SerializedName("searchResult")
    private Boolean searchResult;

    public String getUserNick() {
        return userNick;
    }

    public String getUserStatusMsg() {
        return userStatusMsg;
    }

    public String getUserProfileImg() {
        return userProfileImg;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getSearchResult() {
        return searchResult;
    }
}
