package com.example.dtalk.register;

import static com.example.dtalk.SMS.SMS_timer.timerRunning;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.example.dtalk.Manifest;
import com.example.dtalk.R;
import com.example.dtalk.SMS.SMS_timer;
import com.example.dtalk.SMS.sendSMS;
import com.example.dtalk.information_use;
import com.example.dtalk.login;
import com.example.dtalk.retrofit.GJoinData;
import com.example.dtalk.retrofit.GJoinResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.SMSVerifiData;
import com.example.dtalk.retrofit.SMSVerifiResponse;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.tos;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class signUp extends AppCompatActivity {

    static final int SMS_SEND_PERMISSION = 1;
    private CountDownTimer timer;
    //    private boolean isTimerRunning = false;
    Handler timerHendler;
    private ServerApi service;

    sendSMS sendSMS;
    Boolean SMSVerifi_conn = false;
    SMS_timer SMS_timer;
    Handler handler;


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
        //smsmanager
        sendSMS = new sendSMS();
        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient().create(ServerApi.class);
        //카운트다운 텍스트뷰
        TextView count = findViewById(R.id.count);
        //타이머 객체 생성


        PermissionListener permissionListener = new PermissionListener() {
            //권한 존재하면 넘어가는 메소드
            @Override
            public void onPermissionGranted() {
                Log.e("권한", "권한 허가 상태");


            }

            //권한이 없으면 실행되는 메소드 (메시지 전송 권한 요청
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(signUp.this, "권한을 허용하셔야 문자 인증이 가능합니다.", Toast.LENGTH_SHORT).show();
                finish(); //뒤로가기
                Log.e("권한", "권한 거부 상태");
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionListener)
                .setRationaleMessage("권한 허용이 필요합니다.")
                .setDeniedMessage("권한 허용 거부 시 진행 불가 [설정] > [권한] 에서 권한을 허용 가능합니다.")
                .setPermissions(Manifest.permission.SEND_SMS)
                .check();

        //인증번호 발송 버튼 클릭 시
        send_certification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //문자 보내기 권한 확인
                Log.d("TAG", "onClick: 눌렸음");
                //문자 보내기 기능 구현
                //핸드폰번호 가져오기
                String phone_num = phone_number_input.getText().toString();
                //핸드폰번호가 11자리가 아닐시
                if (phone_num.length() < 11) {
                    Toast.makeText(signUp.this, "010XXXXXXXX 형식으로 입력 해주세요", Toast.LENGTH_SHORT).show();

                } else {
                    phone_number_input.setEnabled(false); //휴대폰번호 입력란 비활성화
                    certification_input.setEnabled(true); //인증번호 입력란 활성화

                    //온리스타트에서 시간 서버랑 재통신해서 남은시간 가져와야함
                    //온 리스타트에서 타이머러닝이 트루면 서버와 재통신해서 남은시간 가져와서 타이머 재실행
//                    SMS_timer = new SMS_timer(getMainLooper(), 0, 5, count);
//                    SMS_timer.startTimer();

                    //서버와 통신
                    SMSVerifi(new SMSVerifiData(phone_num), phone_num, count);

//                            if(timerRunning){ //서버와 통신후 타이머 시작요청을 알리면
//                                SMS_timer.timer(count);
////                                타이머 시작
//                            }

                }
            }
        });

        certification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //인증번호 카운트다운
                TextView count = findViewById(R.id.count);

                SMS_timer = new SMS_timer(getMainLooper(),0,5,count);
                SMS_timer.startTimer();
            }
        });


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lifeCycle", "onRestart: 재시작");
        if (timerRunning == true) { //타이머가 실행중이었으면

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("lifeCycle", "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("lifeCycle", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("lifeCycle", "onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("lifeCycle", "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("lifeCycle", "onResume()");
    }



    private void SMSVerifi(SMSVerifiData data, String phoneNum, TextView countText) {
        service.SMSVerifi(data).enqueue(new Callback<SMSVerifiResponse>() {
            @Override
            public void onResponse(Call<SMSVerifiResponse> call, Response<SMSVerifiResponse> response) {
                SMSVerifiResponse result = response.body();
//                TextView count = findViewById(R.id.count);
                //onresponse에선 ui업데이트 작업이 안되는것같은데 (ui스레드에서 작동하는 메소드이기때문에 sleep에서 문제가 생기는것같다)

                //카운트다운 시작
                //처음엔 카운트가 5 : 00 임 구분은 timerRunning변수로 첫실행인지 홈화면 갔다가 온건지 구분

                if (result.getMessage().equals("false")) { //인증정보가 이미 존재 할 경우

                    Toast.makeText(signUp.this, "조금 뒤에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                } else {
                    //인증번호 발송
                    sendSMS.send(phoneNum, result.getMessage(), signUp.this, signUp.this.getClass());
//                    if (response.isSuccessful()) { //통신이 성공적으로 완료되면
//                        //인증번호 카운트다운
//                        SMS_timer.timer(countText);
//                    }
                    if (response.isSuccessful()) {
                        SMS_timer = new SMS_timer(getMainLooper(), 0, 5, countText);
                        SMS_timer.startTimer();
                    }
                }
            }

            @Override
            public void onFailure(Call<SMSVerifiResponse> call, Throwable t) {
                Toast.makeText(signUp.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("서버 에러 발생", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌

            }
        });
    }
}

//일단은 카운트다운 기능까지만 구현하고 내일 서버와 통신해서 시간계산하는기능 넣어보기