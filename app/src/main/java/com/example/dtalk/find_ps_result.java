package com.example.dtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.findIdResultResponse;
import com.example.dtalk.retrofit.findPsResultData;
import com.example.dtalk.retrofit.findPsResultResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class find_ps_result extends AppCompatActivity {
    String phoneNum;
    private ServerApi service;
    private SharedPreferences preferences;
    TextView resultText;
    TextView result_guide;
    Button login_btn;
    String userId;
    String userPs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ps_result);

        resultText = find_ps_result.this.findViewById(R.id.result);
        result_guide = find_ps_result.this.findViewById(R.id.result_guide);
        login_btn = find_ps_result.this.findViewById(R.id.login_btn);


        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);
        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);

        Intent phoneIntent = getIntent();
        phoneNum = phoneIntent.getStringExtra("phoneNum"); //받아온 핸드폰번호
        userId = phoneIntent.getStringExtra("userId"); //받아온 아이디

        service.findPsResult(new findPsResultData(userId,phoneNum)).enqueue(new Callback<findPsResultResponse>() { //찾은 비밀번호 출력
            @Override
            public void onResponse(Call<findPsResultResponse> call, Response<findPsResultResponse> response) {
                findPsResultResponse result = response.body();
                if(result.getStatus().equals("success")){ //아이디를 찾았을때
                    userPs = result.getPs();
                    resultText.setText(userPs);

                }else if (result.getStatus().equals("error")) { //서버통신오류
                    Toast.makeText(find_ps_result.this, "통신 오류", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<findPsResultResponse> call, Throwable t) {

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {//로그인 버튼 클릭시
            @Override
            public void onClick(View v) {
                //로그인화면 이동
                Intent intent = new Intent(find_ps_result.this, login.class);
                startActivity(intent);


            }
        });




    }
}