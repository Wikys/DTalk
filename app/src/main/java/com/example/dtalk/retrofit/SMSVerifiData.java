package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//sms 인증번호 발급 서버로 보낼 데이터

public class SMSVerifiData {
    @SerializedName("phone_num")
    String phone_num;

    public SMSVerifiData(String phone_num) {
        this.phone_num = phone_num;
    }
}
