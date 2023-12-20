package com.example.dtalk;

import static com.example.dtalk.retrofit.RetrofitClient.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dtalk.retrofit.JWTCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.userInformationSearchResponse;
import com.example.dtalk.searchFriend.add_friend;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class profile extends AppCompatActivity {
    private ImageButton exit_btn;
    private ImageView profile_image;
    private TextView profile_nick;
    private TextView status_message;
    private View one_to_one_chat_btn;
    private View ignore_user_btn;
    private ServerApi service;
    private SharedPreferences preferences;
    private String userId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        exit_btn = (ImageButton)findViewById(R.id.exit_btn);//종료버튼
        profile_image = (ImageView)findViewById(R.id.profile_image);//프로필사진
        profile_nick = (TextView)findViewById(R.id.profile_nick);//닉네임
        status_message =(TextView)findViewById(R.id.status_message);//상태메시지
        one_to_one_chat_btn = (View)findViewById(R.id.one_to_one_chat_btn);//1:1채팅 버튼
        ignore_user_btn = (View)findViewById(R.id.ignore_user_btn); //차단버튼

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);
        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);

        //쉐어드에서 JWT 가져오기
        String Token = preferences.getString("JWT", ""); // 토큰값 가져오기 없으면 ""
        if (!(Token.equals(""))) { //토큰이 존재하면
            //JWT 검증
            service.JWTCheck().enqueue(new Callback<JWTCheckResponse>() {
                @Override
                public void onResponse(Call<JWTCheckResponse> call, Response<JWTCheckResponse> response) {
                    JWTCheckResponse JWTCheckResponse = response.body();

                    //엑세스토큰이 존재하고 유효할경우 혹은 만료되서 리프레시 토큰으로 재발급 받았을경우
                    if (JWTCheckResponse.getStatus().equals("certification_valid")) {
                        Toast.makeText(profile.this, "엑세스 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                    } else if (JWTCheckResponse.getStatus().equals("hacked")) { //비정상적인 접근시
                        Toast.makeText(profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        //액티비티 테스크를 전부 지우고 로그인화면으로 이동
                        Intent intent = new Intent(profile.this, login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                        startActivity(intent);
                    } else if (JWTCheckResponse.getStatus().equals("error")) { //에러시
                        Toast.makeText(profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (JWTCheckResponse.getStatus().equals("reissued")) { //엑세스토큰 만료되서 재발급시
                        String JWT = JWTCheckResponse.getAccessToken();//JWT
                        //쉐어드에 저장
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("JWT", JWT);
                        editor.commit();

                        Toast.makeText(profile.this, "리프레시 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                        Toast.makeText(profile.this, "엑세스토큰 만료되서 재발급", Toast.LENGTH_SHORT).show();

                    } else if (JWTCheckResponse.getStatus().equals("expired")) { //리프레시토큰 만료시
                        Toast.makeText(profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Log.d("TAG", "JWT토큰 onResponse: " + JWTCheckResponse.getUserId());


                }

                @Override
                public void onFailure(Call<JWTCheckResponse> call, Throwable t) {

                }
            });
        }


        Intent intent = getIntent();
        String friendId = intent.getStringExtra("friendId");
        service.userInformationSearch(friendId,"friendInfo").enqueue(new Callback<userInformationSearchResponse>() {//친구정보 불러오기
            @Override
            public void onResponse(Call<userInformationSearchResponse> call, Response<userInformationSearchResponse> response) {
                userInformationSearchResponse result = response.body();

                // Glide를 사용하여 이미지 로딩
                Glide.with(profile.this)
                        .load(BASE_URL+result.getUserProfileImg()) // friend.getImageUrl()는 이미지의 URL 주소
                        .into(profile_image);
                profile_nick.setText(result.getUserNick().toString());
                status_message.setText(result.getUserStatusMsg());

            }

            @Override
            public void onFailure(Call<userInformationSearchResponse> call, Throwable t) {

            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() { //종료버튼 클릭시
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}