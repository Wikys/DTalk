package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;
//친구추가시 보낼 데이터

public class addFriendData {

    @SerializedName("userId")
    private String userId;

    @SerializedName("friendId")
    private String friendId;

    public addFriendData(String userId, String friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
