package com.example.dtalk.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServerApi {
    @POST("/DTalk/GoogleLogin.php") //구글 로그인
    Call<GLoginResponse> GUserLogin(@Body GLoginData data);

    @POST("/DTalk/GoogleRegister.php") //구글 회원가입
    Call<GJoinResponse> GUserJoin(@Body GJoinData data);

    @POST("/DTalk/verification_codes.php") //SMS 인증번호 발급
    Call<SMSVerifiResponse> SMSVerifi(@Body SMSVerifiData data);
}
