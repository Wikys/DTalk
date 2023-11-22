package com.example.dtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.SMSVerifiResponse;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.findIdResultResponse;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class find_id_result extends AppCompatActivity {
    String phoneNum;
    private ServerApi service;
    private SharedPreferences preferences;
    TextView resultText;
    TextView result_guide;
    Button login_btn;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_result);
        resultText = find_id_result.this.findViewById(R.id.result);
        result_guide = find_id_result.this.findViewById(R.id.result_guide);
        login_btn = find_id_result.this.findViewById(R.id.login_btn);

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);
        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);

        Intent phoneIntent = getIntent();
        phoneNum = phoneIntent.getStringExtra("phoneNum"); //받아온 핸드폰번호


        service.findIdResult(phoneNum).enqueue(new Callback<findIdResultResponse>() { //찾은 아이디 출력
            @Override
            public void onResponse(Call<findIdResultResponse> call, Response<findIdResultResponse> response) {
                findIdResultResponse result = response.body();
                if(result.getStatus().equals("success")){ //아이디를 찾았을때
                    userId = result.getID();
                    resultText.setText(userId);

                } else if (result.getStatus().equals("failure")) { //검색결과가 없을때
                    String message = result.getMessage();
                    result_guide.setText("");
                    resultText.setText(message);

                } else if (result.getStatus().equals("error")) { //서버통신오류
                    Toast.makeText(find_id_result.this, "통신 오류", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<findIdResultResponse> call, Throwable t) {

            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {//로그인 버튼 클릭시
            @Override
            public void onClick(View v) {
                //로그인화면 이동
                Toast.makeText(find_id_result.this, "비밀번호가 성공적으로 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(find_id_result.this, login.class);
                intent.putExtra("userId",userId);
                startActivity(intent);


            }
        });

    }

}