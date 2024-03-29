package com.example.dtalk.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @GET("/DTalk/JWT_check.php") //JWT 체크요청
    Call<JWTCheckResponse> JWTCheck();

    @GET("/DTalk/find_id_result.php") //아이디 찾기 결과 요청
    Call<findIdResultResponse> findIdResult(@Query("phone_num") String phone_num);

    @POST("/DTalk/find_ps_result.php") //비밀번호 찾기 결과 요청
    Call<findPsResultResponse> findPsResult(@Body findPsResultData data);

    @GET("/DTalk/user_information_search.php") //계정정보 불러오기 //스테이터스로 줘야할정보 구분
    Call<userInformationSearchResponse> userInformationSearch(@Query("userId") String userId,
                                                                      @Query("status") String status);

    @GET("/DTalk/add_friend_search.php") //아이디 검색(친구추가)
    Call<addFriendSearchResponse> addFriendSearch(@Query("userId") String userId,
                                                  @Query("friendId") String friendId);

    @POST("/DTalk/add_friend.php") //친구 추가 버튼 클릭했을때 친구추가 요청하는 기능
    Call<addFriendResponse> addFriend(@Body addFriendData data);

    @DELETE("/DTalk/logout.php") //로그아웃 버튼 클릭했을때 로그아웃 하는 기능
    Call<logoutResponse> logout(@Query("userId") String userId);

    @GET("/DTalk/friends_list_check.php") //유저의 친구목록 검색해서 가져오는기능
    Call<friendsListCheckResponse> friendsListCheck(@Query("userId") String userId);

    @Multipart
    @POST("/DTalk/editProfile.php") //프로필 변경 버튼 눌렀을떄 프로필 변경사항 저장해주는 버튼
    Call<editProfileResponse> editProfile(@Part MultipartBody.Part profileImg,
                                          @Part("userNick")RequestBody userNick,
                                          @Part("userStatus")RequestBody userStatus,
                                          @Part("sep")RequestBody sep,
                                          @Part("userId")RequestBody userId);


}
