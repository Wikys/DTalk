package com.example.dtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtalk.SMS.SMS_timer;
import com.example.dtalk.SMS.sendSMS;
import com.example.dtalk.register.signUp;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.SMSVerifiData;
import com.example.dtalk.retrofit.SMSVerifiResponse;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.certificationCheckResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class find_id extends AppCompatActivity {

    EditText input_phone_number;
    EditText certification_input;
    Button send_certification_btn;
    Button certification_btn;
    Button next_btn;
    private ServerApi service;
    private SharedPreferences preferences;
    sendSMS sendSMS;
    SMS_timer SMS_timer;
    TextView countText;
    boolean certificationCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);
        //smsmanager
        sendSMS = new sendSMS();

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);

        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);

        input_phone_number = find_id.this.findViewById(R.id.input_phone_number); //핸드폰번호 입력란
        certification_input = find_id.this.findViewById(R.id.certification_input); //인증번호 입력란
        send_certification_btn = find_id.this.findViewById(R.id.send_certification_btn); //인증번호 발송 버튼
        certification_btn = find_id.this.findViewById(R.id.certification_btn); //인증번호 인증 버튼
        next_btn = find_id.this.findViewById(R.id.next_btn); // 다음 버튼(next)
        countText = find_id.this.findViewById(R.id.count); // 카운트다운 텍스트뷰
        //인증번호 인증 확인 변수
        certificationCheck = false;


        send_certification_btn.setOnClickListener(new View.OnClickListener() { //인증번호 발송버튼 클릭시
            @Override
            public void onClick(View v) {
                String phoneNum = input_phone_number.getText().toString(); //유저 폰번호
                //서버에서 폰번호 확인절차 들어가야함
                if(!(phoneNum.equals(""))){//폰번호 입력란이 비어있지 않을때
                    service.SMSVerifi(new SMSVerifiData(phoneNum,"find")).enqueue(new Callback<SMSVerifiResponse>() { //핸드폰번호를 넣어 서버와 통신시작
                        @Override
                        public void onResponse(Call<SMSVerifiResponse> call, Response<SMSVerifiResponse> response) {
                            SMSVerifiResponse result = response.body();

                            //카운트다운 시작
                            //처음엔 카운트가 5 : 00 임 구분은 timerRunning변수로 첫실행인지 홈화면 갔다가 온건지 구분

                            if (result.getMessage().equals("false")) { //인증정보가 이미 존재 할 경우

                                Toast.makeText(find_id.this, "조금 뒤에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                            }else if(result.getMessage().equals("fail")){
                                Toast.makeText(find_id.this, "이미 존재하는 아이디 입니다", Toast.LENGTH_SHORT).show();
                            }else {

                                input_phone_number.setEnabled(false); //휴대폰번호 입력란 비활성화
                                certification_input.setEnabled(true); //인증번호 입력란 활성화
                                certification_btn.setEnabled(true); //인증버튼 활성화
                                //인증번호 발송
                                sendSMS.send(phoneNum, result.getMessage(), find_id.this, find_id.this.getClass());
                                int count = Integer.parseInt(result.getCount());
                                //타이머 스타트
                                SMS_timer = new SMS_timer(getMainLooper(), 0, count, countText,certification_btn);
                                SMS_timer.startTimer();
                            }
                        }
                        @Override
                        public void onFailure(Call<SMSVerifiResponse> call, Throwable t) {

                        }
                    });

                }else{
                    Toast.makeText(find_id.this, "핸드폰 번호를 입력 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        certification_btn.setOnClickListener(new View.OnClickListener() { //인증버튼 클릭시
            @Override
            public void onClick(View v) {
                String phoneNum = input_phone_number.getText().toString(); //유저 폰번호
                String certificationNum = certification_input.getText().toString(); //인증번호 입력칸

                if(certificationNum.equals("")){ //인증번호 빈칸일시
                    Toast.makeText(find_id.this, "인증 번호를 입력 해주세요!", Toast.LENGTH_SHORT).show();
                }else{
                    //서버와 통신해서 인증번호 검증
                    service.certificationCheck(certificationNum,phoneNum).enqueue(new Callback<certificationCheckResponse>() {
                        @Override
                        public void onResponse(Call<certificationCheckResponse> call, Response<certificationCheckResponse> response) {

                            if (response.isSuccessful() && response.body() != null) {//통신 성공시,반환메시지가 null이 아닐시ㅁㅈ
                                certificationCheckResponse certificationCheckResponse = response.body();
                                String message = certificationCheckResponse.getMessage(); // 반환된 결과 메시지
                                int code = certificationCheckResponse.getCode(); //반환된 상태값 0 : 성공, 1 : 실패

                                if (code == 0){
                                    //인증번호 일치했다는 메시지 출력후 인증번호 발송버튼 인증버튼 가리기
                                    send_certification_btn.setVisibility(View.GONE); //인증번호 관련버튼 숨김
                                    certification_btn.setVisibility(View.GONE);
                                    certification_input.setEnabled(false);
                                    //인증상태 완료 변수 변경
                                    certificationCheck = true;
                                    //타이머 정지
                                    SMS_timer.stopTimer();
                                    Toast.makeText(find_id.this, message, Toast.LENGTH_SHORT).show();
                                }else if (code == 1){
                                    //인증번호 틀렸다는 메시지
                                    Toast.makeText(find_id.this, message, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(find_id.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(find_id.this, "서버 통신 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<certificationCheckResponse> call, Throwable t) {

                            Log.d("TAG", "onFailure: 휴대폰 번호 인증 서버 통신 실패");

                        }
                    });
                }

            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() { //결과창 넘어가는 화면
            @Override
            public void onClick(View v) {
                //모든 정보가 작성된상태일때
                if(certificationCheck){ //인증을 완료한 상태일때
                    String phoneNum = input_phone_number.getText().toString(); //유저 폰번호

                    Intent intent = new Intent(find_id.this, find_id_result.class);
                    intent.putExtra("phoneNum",phoneNum);
                    startActivity(intent);

                }
            }
        });




    }
}