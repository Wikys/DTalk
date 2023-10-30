package com.example.dtalk.register;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.example.dtalk.Manifest;
import com.example.dtalk.R;
import com.example.dtalk.information_use;
import com.example.dtalk.login;
import com.example.dtalk.tos;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class signUp extends AppCompatActivity {

    static final int SMS_SEND_PERMISSION = 1;
    private CountDownTimer timer;
    private boolean isTimerRunning = false;
    boolean timerRunning = false;
    Handler timerHendler;

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
        EditText certification_input = findViewById(R.id.certification_input);


        //인증번호 발송 버튼 클릭 시
        send_certification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //문자 보내기 권한 확인
                Log.d("TAG", "onClick: 눌렸음");

                PermissionListener permissionListener = new PermissionListener() {
                    //권한 존재하면 넘어가는 메소드
                    @Override
                    public void onPermissionGranted() {
                        Log.e("권한", "권한 허가 상태");
                        //문자 보내기 기능 구현
                        //핸드폰번호 가져오기
                        String phone_num = phone_number_input.getText().toString();
                        Log.d("TAG", "onPermissionGranted: " + phone_num.length());

                        //핸드폰번호가 11자리가 아닐시
                        if (phone_num.length() < 11) {
                            Toast.makeText(signUp.this, "010XXXXXXXX 형식으로 입력 해주세요", Toast.LENGTH_SHORT).show();

                        } else {
                            phone_number_input.setEnabled(false); //휴대폰번호 입력란 비활성화
                            certification_input.setEnabled(true); //인증번호 입력란 활성화


                            int min;
                            int sec;


                            //온리스타트에서 시간 서버랑 재통신해서 남은시간 가져와야함
                            //온 리스타트에서 타이머러닝이 트루면 서버와 재통신해서 남은시간 가져와서 타이머 재실행


                        }


                    }

                    //권한이 없으면 실행되는 메소드 (메시지 전송 권한 요청
                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(signUp.this, "권한을 허용하셔야 앱을 이용 하실수 있습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("권한", "권한 거부 상태");
                    }
                };
                TedPermission.create()
                        .setPermissionListener(permissionListener)
                        .setRationaleMessage("권한 허용이 필요합니다.")
                        .setDeniedMessage("권한 허용 거부 시 진행 불가 [설정] > [권한] 에서 권한을 허용 가능합니다.")
                        .setPermissions(Manifest.permission.SEND_SMS)
                        .check();

            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (timerRunning == true) { //타이머가 실행중이었으면

        }
    }

    private void timer() {
        //처음엔 카운트가 5 : 00 임 구분은 timerRunning변수로 첫실행인지 홈화면 갔다가 온건지 구분

        if (timerRunning == false) { // 타이머 첫실행이거나 끝나고나서 재실행할때
            timerRunning = true;
            timerHendler = new Handler(Looper.getMainLooper());

            //인증번호 카운트다운
            TextView count = findViewById(R.id.count);
            count.setVisibility(View.VISIBLE); //카운트다운 보이게하기




        }
    }
}

//일단은 카운트다운 기능까지만 구현하고 내일 서버와 통신해서 시간계산하는기능 넣어보기