package com.example.dtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.dtalk.retrofit.findPsResultData;
import com.example.dtalk.retrofit.findPsResultResponse;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class find_ps extends AppCompatActivity {

    private ServerApi service;
    private SharedPreferences preferences;
    sendSMS sendSMS;
    SMS_timer SMS_timer;
    boolean certificationCheck;
    EditText input_id;
    EditText input_phone_number;
    Button send_certification_btn;
    EditText certification_input;
    Button certification_btn;
    Button next_btn;
    TextView countText;
    String userId;
    String phoneNum;
    Boolean psCheck;
    TextView input_ps;
    EditText ps_input_confirm;
    TextView ps_check_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ps);
        input_id = find_ps.this.findViewById(R.id.input_id); //아이디 입력창
        input_phone_number = find_ps.this.findViewById(R.id.input_phone_number); //핸드폰번호 입력창
        send_certification_btn = find_ps.this.findViewById(R.id.send_certification_btn); //인증번호 발송 버튼
        certification_input = find_ps.this.findViewById(R.id.certification_input); // 인증번호 입력창
        certification_btn = find_ps.this.findViewById(R.id.certification_btn); //인증번호 인증 버튼
        next_btn = find_ps.this.findViewById(R.id.next_btn); //다음 버튼
        countText = find_ps.this.findViewById(R.id.count); //인증번호 유효시간 카운트다운
        //비밀번호 인풋
        input_ps = (TextView) find_ps.this.findViewById(R.id.input_ps);
        //비밀번호 확인 인풋
        ps_input_confirm = (EditText) find_ps.this.findViewById(R.id.ps_input_confirm);
        //비밀번호 일치 확인 텍스트뷰
        ps_check_text = (TextView) find_ps.this.findViewById(R.id.ps_check_text);
        psCheck = false;

        //smsmanager
        sendSMS = new sendSMS();

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);

        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);

        //인증번호 인증 확인 변수
        certificationCheck = false;

        PermissionListener permissionListener = new PermissionListener() {
            //권한 존재하면 넘어가는 메소드
            @Override
            public void onPermissionGranted() {
                Log.e("권한", "권한 허가 상태");


            }

            //권한이 없으면 실행되는 메소드 (메시지 전송 권한 요청
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(find_ps.this, "권한을 허용하셔야 문자 인증이 가능합니다.", Toast.LENGTH_SHORT).show();
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

        send_certification_btn.setOnClickListener(new View.OnClickListener() { //인증번호 발송버튼 클릭시
            @Override
            public void onClick(View v) {

                phoneNum = input_phone_number.getText().toString(); //유저 폰번호
                userId = input_id.getText().toString();//유저 아이디
                //서버에서 폰번호 확인절차 들어가야함
                if(!(phoneNum.equals("")) && !(userId.equals(""))){//폰번호 입력란,아이디 입력란이 비어있지 않을때
                    service.SMSVerifi(new SMSVerifiData(phoneNum,"findPs",userId)).enqueue(new Callback<SMSVerifiResponse>() { //핸드폰번호를 넣어 서버와 통신시작
                        @Override
                        public void onResponse(Call<SMSVerifiResponse> call, Response<SMSVerifiResponse> response) {
                            SMSVerifiResponse result = response.body();

                            //카운트다운 시작
                            //처음엔 카운트가 5 : 00 임 구분은 timerRunning변수로 첫실행인지 홈화면 갔다가 온건지 구분

                            if (result.getMessage().equals("false")) { //인증정보가 이미 존재 할 경우

                                Toast.makeText(find_ps.this, "조금 뒤에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                            } else if (result.getMessage().equals("fail")) { //인증정보가 잘못입력되었거나 존재하지않을때
                                Toast.makeText(find_ps.this, "해당 번호로 가입 된 계정 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();

                            } else {

                                input_phone_number.setEnabled(false); //휴대폰번호 입력란 비활성화
                                certification_input.setEnabled(true); //인증번호 입력란 활성화
                                certification_btn.setEnabled(true); //인증버튼 활성화
                                //인증번호 발송
                                sendSMS.send(phoneNum, result.getMessage(), find_ps.this, find_ps.this.getClass());
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
                    Toast.makeText(find_ps.this, "핸드폰번호와 아이디를 작성 해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        certification_btn.setOnClickListener(new View.OnClickListener() { //인증버튼 클릭시
            @Override
            public void onClick(View v) {
                String phoneNum = input_phone_number.getText().toString(); //유저 폰번호
                String certificationNum = certification_input.getText().toString(); //인증번호 입력칸

                if(certificationNum.equals("")){ //인증번호 빈칸일시
                    Toast.makeText(find_ps.this, "인증 번호를 입력 해주세요!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(find_ps.this, message, Toast.LENGTH_SHORT).show();
                                }else if (code == 1){
                                    //인증번호 틀렸다는 메시지
                                    Toast.makeText(find_ps.this, message, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(find_ps.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(find_ps.this, "서버 통신 실패", Toast.LENGTH_SHORT).show();
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
        input_ps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPassword(input_ps, ps_input_confirm, ps_check_text);
            }
        });

        ps_input_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPassword(input_ps, ps_input_confirm, ps_check_text);
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() { //결과창 넘어가는 화면
            @Override
            public void onClick(View v) {
                //모든 정보가 작성된상태일때
                if(certificationCheck && psCheck){ //인증을 완료한 상태일때, 비밀번호 확인이 완료된 상태일때
                    String userPs = input_ps.getText().toString();

                    service.findPsResult(new findPsResultData(userId,userPs)).enqueue(new Callback<findPsResultResponse>() { //비밀번호 변경
                        @Override
                        public void onResponse(Call<findPsResultResponse> call, Response<findPsResultResponse> response) {
                            findPsResultResponse result = response.body();
                            if(result.getStatus().equals("success")){ //성공했을때
//                                비밀번호를 변경하고
                                //메인으로 이동
                                Intent intent = new Intent(find_ps.this, login.class);
                                Toast.makeText(find_ps.this, "비밀번호가 성공적으로 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                                startActivity(intent);

                            }else if (result.getStatus().equals("error")) { //서버통신오류
                                Toast.makeText(find_ps.this, "통신 오류", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<findPsResultResponse> call, Throwable t) {

                        }
                    });

                }else{
                    Toast.makeText(find_ps.this, "정보를 입력 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void checkPassword(TextView input_ps, TextView ps_input_confirm, TextView ps_check_text) {

        String inputPs = input_ps.getText().toString();
        String confirmPs = ps_input_confirm.getText().toString();

        if (inputPs.equals("") || confirmPs.equals("")) { // 둘중 하나라도 값이 없으면 텍스트지우고 변수false
            ps_check_text.setText("");
            psCheck = false;
        } else {
            if (inputPs.equals(confirmPs)) {
                ps_check_text.setText("비밀번호가 일치합니다.");
                psCheck = true;
            } else {
                ps_check_text.setText("비밀번호가 일치하지 않습니다.");
                psCheck = false;
            }
        }
    }
}