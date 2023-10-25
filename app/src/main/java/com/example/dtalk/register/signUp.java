package com.example.dtalk.register;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.example.dtalk.Manifest;
import com.example.dtalk.R;
import com.example.dtalk.login;
import com.example.dtalk.tos;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class signUp extends AppCompatActivity {

    static final int SMS_SEND_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //인증번호 발송 버튼
        Button send_certification_btn = (Button) signUp.this.findViewById(R.id.send_certification_btn);
        //인증확인 버튼
        Button certification_btn = (Button) signUp.this.findViewById(R.id.certification_btn);
        //회원가입 버튼
        Button signUp_btn = (Button) signUp.this.findViewById(R.id.signUp_btn);
        //핸드폰 번호 입력칸
        EditText phone_number_input = findViewById(R.id.phone_number_input);
        //인증번호 입력칸


        //인증번호 발송 버튼 클릭 시
        send_certification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //문자 보내기 권한 확인
                Log.d("TAG", "onClick: 눌렸음");
                int permissionCheck = ContextCompat.checkSelfPermission(signUp.this, android.Manifest.permission.SEND_SMS);

                if(permissionCheck != PackageManager.PERMISSION_GRANTED){
                    //문자 보내기 권한 거부
                    if(ActivityCompat.shouldShowRequestPermissionRationale(signUp.this,android.Manifest.permission.SEND_SMS)){
                        Toast.makeText(signUp.this, "SMS 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                    //문자 보내기 권한 허용 요청
                    ActivityCompat.requestPermissions(signUp.this,new String[]{android.Manifest.permission.SEND_SMS},SMS_SEND_PERMISSION);
                } //그냥 이부분 테드퍼미션으로 다시하기

            }
        });


    }



}