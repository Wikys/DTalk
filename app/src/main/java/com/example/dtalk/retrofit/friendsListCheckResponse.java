package com.example.dtalk.retrofit;

//유저 친구목록 반환

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class friendsListCheckResponse {

    @SerializedName("friends")
    private List<Friend> friends;

    public List<Friend> getFriends() {
        return friends;
    }

    public class Friend {

        @SerializedName("friendID")
        private String friendID;

        @SerializedName("userName")
        private String userName;

        @SerializedName("userStatusMsg")
        private String userStatusMsg;

        @SerializedName("userProfileImage")
        private String userProfileImage;

        public String getFriendID() {
            return friendID;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserStatusMsg() {
            return userStatusMsg;
        }

        public String getUserProfileImage() {
            return userProfileImage;
        }
    }

//    @SerializedName("profileImg")
//    private int profileImg;
//    @SerializedName("nick")
//    private String nick;
//    @SerializedName("statusMessage")
//    private String statusMessage;
//
    @SerializedName("status")
    private String status;
//
//    public int getProfileImg() {
//        return profileImg;
//    }
//
//    public String getNick() {
//        return nick;
//    }
//
//    public String getStatusMessage() {
//        return statusMessage;
//    }
//
    public String getStatus() {
        return status;
    }
}
