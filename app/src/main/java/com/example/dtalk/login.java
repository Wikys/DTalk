package com.example.dtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        Button sign_up_btn = (Button) login.this.findViewById(R.id.signUp_btn);
        //회원가입 버튼
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            //이용약관 동의 화면 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, tos.class);
                startActivity(intent);
            }
        });

        //구글 로그인 버튼
        Button google_sns_login_btn = (Button) login.this.findViewById(R.id.google_sns_login_btn);
        google_sns_login_btn.setOnClickListener(new View.OnClickListener() {
            //구글 로그인 기능
            @Override
            public void onClick(View view) {

            }
        });

    }

}