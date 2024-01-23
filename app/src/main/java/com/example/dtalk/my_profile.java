package com.example.dtalk;

import static com.example.dtalk.retrofit.RetrofitClient.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dtalk.retrofit.JWTCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.userInformationSearchResponse;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class my_profile extends AppCompatActivity {
    private String userId;
    private ImageButton edit_profile_background_btn;
    private ImageButton exit_btn;
    private ServerApi service;
    private SharedPreferences preferences;
    private ImageView profile_image;
    private TextView profile_nick;
    private TextView status_message;
    private View edit_profile_btn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        edit_profile_background_btn = this.findViewById(R.id.edit_profile_background_btn);//배경변경버튼
        exit_btn = this.findViewById(R.id.exit_btn);//나가기버튼
        profile_image = this.findViewById(R.id.profile_image);//프로필 이미지
        profile_nick = this.findViewById(R.id.profile_nick);//프로필 닉네임
        status_message =this.findViewById(R.id.status_message);//프로필 상태메시지
        edit_profile_btn = this.findViewById(R.id.edit_profile_btn); // 프로필 편집 버튼


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
                        Toast.makeText(my_profile.this, "엑세스 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                    } else if (JWTCheckResponse.getStatus().equals("hacked")) { //비정상적인 접근시
                        Toast.makeText(my_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        //액티비티 테스크를 전부 지우고 로그인화면으로 이동
                        Intent intent = new Intent(my_profile.this, login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                        startActivity(intent);
                    } else if (JWTCheckResponse.getStatus().equals("error")) { //에러시
                        Toast.makeText(my_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (JWTCheckResponse.getStatus().equals("reissued")) { //엑세스토큰 만료되서 재발급시
                        String JWT = JWTCheckResponse.getAccessToken();//JWT
                        //쉐어드에 저장
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("JWT", JWT);
                        editor.commit();

                        Toast.makeText(my_profile.this, "리프레시 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                        Toast.makeText(my_profile.this, "엑세스토큰 만료되서 재발급", Toast.LENGTH_SHORT).show();

                    } else if (JWTCheckResponse.getStatus().equals("expired")) { //리프레시토큰 만료시
                        Toast.makeText(my_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Log.d("TAG", "JWT토큰 onResponse: " + JWTCheckResponse.getUserId());


                }

                @Override
                public void onFailure(Call<JWTCheckResponse> call, Throwable t) {

                }
            });
        }

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId"); //사용자 아이디 받아옴
        service.userInformationSearch(userId,"myInfo").enqueue(new Callback<userInformationSearchResponse>() {//친구정보 불러오기
            @Override
            public void onResponse(Call<userInformationSearchResponse> call, Response<userInformationSearchResponse> response) {
                userInformationSearchResponse result = response.body();

                //이미지 변경시 캐시된 이전 이미지를 사용하는 경우 방지하기위해 랜덤 쿼리 매개변수를 먹여줌
                String imageUrl = BASE_URL+result.getUserProfileImg();
                String imageUrlWithRandomQuery = imageUrl + "?timestamp=" + System.currentTimeMillis();


                // Glide를 사용하여 이미지 로딩
                Glide.with(my_profile.this)
                        .load(imageUrlWithRandomQuery) // friend.getImageUrl()는 이미지의 URL 주소
                        .into(profile_image);
                Log.d("TAG", "onResponse: "+BASE_URL+result.getUserProfileImg());
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
                Intent intent = new Intent(my_profile.this, navi.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                startActivity(intent); //친구목록 화면으로 넘어감
            }
        });

        edit_profile_background_btn.setOnClickListener(new View.OnClickListener() { //배경 변경버튼 클릭시
            @Override
            public void onClick(View v) {

            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() { //프로필 편집버튼 클릭시
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(my_profile.this,edit_profile.class); //프로필 변경화면으로 이동
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });



    }
}