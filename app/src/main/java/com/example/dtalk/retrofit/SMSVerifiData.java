package com.example.dtalk.retrofit;

import com.google.gson.annotations.SerializedName;

//sms 인증번호 발급 서버로 보낼 데이터

public class SMSVerifiData {
    @SerializedName("phone_num")
    String phone_num;

    @SerializedName("sep") //회원가입인지 아이디 비밀번호 찾기인지 구분
    String sep;

    public SMSVerifiData(String phone_num,String sep) {
        this.phone_num = phone_num;
        this.sep = sep;
    }
}
