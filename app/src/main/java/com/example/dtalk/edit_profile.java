package com.example.dtalk;

import static com.example.dtalk.retrofit.RetrofitClient.BASE_URL;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dtalk.retrofit.JWTCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.userInformationSearchResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_profile extends AppCompatActivity {
    private String userId;
    private SharedPreferences preferences;
    private ServerApi service;
    private ImageView profile_image;
    private EditText input_nick;
    private EditText input_status_message;
    private Button confirm_btn;
    private Button cancel_btn;
    private String[] item;
    private AlertDialog btn_dialog;
    private AlertDialog.Builder builder;
    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);
        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);

        profile_image = edit_profile.this.findViewById(R.id.profile_image); //프로필 이미지
        input_nick = edit_profile.this.findViewById(R.id.input_nick);//닉네임 인풋
        input_status_message = edit_profile.this.findViewById(R.id.input_status_message);//상태메시지 인풋
        confirm_btn = edit_profile.this.findViewById(R.id.confirm_btn); //프로필 변경 버튼
        cancel_btn = edit_profile.this.findViewById(R.id.cancel_btn);//프로필 변경 취소 버튼

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
                        Toast.makeText(edit_profile.this, "엑세스 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                    } else if (JWTCheckResponse.getStatus().equals("hacked")) { //비정상적인 접근시
                        Toast.makeText(edit_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        //액티비티 테스크를 전부 지우고 로그인화면으로 이동
                        Intent intent = new Intent(edit_profile.this, login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                        startActivity(intent);
                    } else if (JWTCheckResponse.getStatus().equals("error")) { //에러시
                        Toast.makeText(edit_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (JWTCheckResponse.getStatus().equals("reissued")) { //엑세스토큰 만료되서 재발급시
                        String JWT = JWTCheckResponse.getAccessToken();//JWT
                        //쉐어드에 저장
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("JWT", JWT);
                        editor.commit();

                        Toast.makeText(edit_profile.this, "리프레시 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                        Toast.makeText(edit_profile.this, "엑세스토큰 만료되서 재발급", Toast.LENGTH_SHORT).show();

                    } else if (JWTCheckResponse.getStatus().equals("expired")) { //리프레시토큰 만료시
                        Toast.makeText(edit_profile.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
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

        // ActivityResultLauncher 초기화
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // 선택한 이미지 처리 코드를 여기에 추가
                        if (uri != null){ //혹시나 사용자가 x눌렀을떄 예외처리용
                            // Glide를 사용하여 이미지 로딩
                            Glide.with(edit_profile.this)
                                    .load(uri) // friend.getImageUrl()는 이미지의 URL 주소
                                    .override(130, 130)//크기 조절
                                    .into(profile_image);
                        }else{
                            //null이면 이미지변경 취소

                        }


                    }
                }
        );

        service.userInformationSearch(userId, "myInfo").enqueue(new Callback<userInformationSearchResponse>() {//친구정보 불러오기
            @Override
            public void onResponse(Call<userInformationSearchResponse> call, Response<userInformationSearchResponse> response) {
                userInformationSearchResponse result = response.body();

                // Glide를 사용하여 이미지 로딩
                Glide.with(edit_profile.this)
                        .load(BASE_URL + result.getUserProfileImg()) // friend.getImageUrl()는 이미지의 URL 주소
                        .into(profile_image);
                input_nick.setText(result.getUserNick().toString());
                input_status_message.setText(result.getUserStatusMsg());

            }

            @Override
            public void onFailure(Call<userInformationSearchResponse> call, Throwable t) {

            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() { //프로필 이미지 클릭시
            @Override
            public void onClick(View v) {
                showDialog();//메뉴 다이얼로그 출력

            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() { //프로필 변경 적용버튼 클릭시
            @Override
            public void onClick(View v) {
                //메인으로 이동하는데 만약에 채팅방에서 들어온거일수도 있으니까
                //나중에 이프문 만들어서 어디로 들어왔냐에따라 구분해서 인텐트 바꿔야할듯?
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {//프로필 변경 취소버튼 클릭시
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void showDialog() { //다이얼로그 실행 함수
        item = getResources().getStringArray(R.array.edit_profile_image);
        builder = new AlertDialog.Builder(edit_profile.this);
        builder.setTitle("프로필 사진 변경");

        //다이얼로그에 리스트 담기
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        //프로필 사진 변경 클릭시 앨범출력


                        openGallery();

                        break;
                    case 1:
                        //기본이미지로 변경 클릭시 프사 기본이미지로 변경
                        // Glide를 사용하여 이미지 로딩
                        Glide.with(edit_profile.this)
                                .load(BASE_URL+"/DTalk/img/default.jpg") // 기본프사
                                .into(profile_image);


                        break;
                }

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    // 앨범 열기 메서드
    private void openGallery() {
        galleryLauncher.launch("image/*"); // 이미지 선택할 수 있는 앨범 열기
    }
}