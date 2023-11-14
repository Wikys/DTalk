package com.example.dtalk.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerApi {
    //형태 call 안에 반환 클래스,메소드이름(본인이결정),보낼데이터 DTO클래스
    @POST("/DTalk/GoogleLogin.php") //구글 로그인
    Call<GLoginResponse> GUserLogin(@Body GLoginData data);

    @POST("/DTalk/GoogleRegister.php") //구글 회원가입
    Call<GJoinResponse> GUserJoin(@Body GJoinData data);

    @POST("/DTalk/verification_codes.php") //SMS 인증번호 발급
    Call<SMSVerifiResponse> SMSVerifi(@Body SMSVerifiData data);

    @GET("/DTalk/ID_check.php") //아이디 중복 체크 (정보검색용이라 겟)
    Call<IDCheckResponse> IDCheck(@Query("userID") String userID );

    @GET("/DTalk/certification_check.php") //인증번호 체크 (정보검색용이라 겟)
    Call<certificationCheckResponse> certificationCheck(@Query("certificationNum") String certificationNum,
                                                        @Query("phone_num") String phone_num);

    @POST("/DTalk/register.php") //일반 회원가입 요청
    Call<registerResponse> register(@Body registerData data);

    @POST("/DTalk/login.php") //일반 로그인 요청
    Call<loginResponse> login(@Body loginData data);


}
