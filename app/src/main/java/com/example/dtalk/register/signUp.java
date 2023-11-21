package com.example.dtalk.register;

import static com.example.dtalk.SMS.SMS_timer.timerRunning;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.dtalk.Manifest;
import com.example.dtalk.R;
import com.example.dtalk.SMS.SMS_timer;
import com.example.dtalk.SMS.sendSMS;
import com.example.dtalk.login;
import com.example.dtalk.retrofit.IDCheckData;
import com.example.dtalk.retrofit.IDCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.SMSVerifiData;
import com.example.dtalk.retrofit.SMSVerifiResponse;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.certificationCheckResponse;
import com.example.dtalk.retrofit.registerData;
import com.example.dtalk.retrofit.registerResponse;
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
    private SharedPreferences preferences;

    sendSMS sendSMS;
    Boolean SMSVerifi_conn = false;
    SMS_timer SMS_timer;
    Handler handler;
    Boolean idCheckResult;
    Boolean psCheck;
    Boolean certificationCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);

        //아이디 인풋
        TextView input_id = (TextView) findViewById(R.id.input_id);
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
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);
        //카운트다운 텍스트뷰
        TextView count = (TextView) findViewById(R.id.count);
        //아이디 중복확인 버튼
        Button id_check = (Button) findViewById(R.id.id_check);
        //아이디 중복체크 완료했는지 확인하는 변수
        idCheckResult = false;
        //비밀번호 일치 확인 변수
        psCheck = false;
        //비밀번호 인풋
        TextView input_ps = (TextView) findViewById(R.id.input_ps);
        //비밀번호 확인 인풋
        EditText ps_input_confirm = (EditText) findViewById(R.id.ps_input_confirm);
        //비밀번호 일치 확인 텍스트뷰
        TextView ps_check_text = (TextView) findViewById(R.id.ps_check_text);
        //인증번호 인증 확인 변수
        certificationCheck = false;
        //닉네임 인풋
        TextView nick_input = (TextView) findViewById(R.id.nick_input);

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

        //아이디 중복확인 버튼 클릭시
        id_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //아이디 중복 체크

                //다이얼로그에서 aa 아이디를 사용하시겠습니까? 하고 예 누르면 아이디칸 잠금되게 설계
                //다이얼로그에서 예를 누르면 변수를 변경하고 온리줌에서 변수체크하고 아이디칸 잠금되게?
                AlertDialog.Builder menu = new AlertDialog.Builder(signUp.this);
                menu.setIcon(R.mipmap.ic_launcher);
                menu.setTitle("아이디 중복 확인"); // 제목
                String userID = input_id.getText().toString();
                Log.d("TAG", "onClick: " + userID);

                if (!userID.equals("")) { //아이디에 공백이 있으면안됨

                    //서버에서 아이디 중복 확인후 출력메시지 결정
                    service.IDCheck(userID).enqueue(new Callback<IDCheckResponse>() {
                        @Override
                        public void onResponse(Call<IDCheckResponse> call, Response<IDCheckResponse> response) {

                            if (response.isSuccessful() && response.body() != null) {//통신 성공시,반환메시지가 null이 아닐시
                                IDCheckResponse idCheckResponse = response.body();
                                String message = idCheckResponse.getMessage(); // 반환된 결과 메시지
                                int code = idCheckResponse.getCode();

                                // AlertDialog.Builder를 사용하여 사용자에게 메시지를 보여줄 수 있습니다.
                                menu.setMessage(message);
                                if (code == 0) { //이미 존재하는 아이디일때
                                    menu.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); //다이얼로그 제거
                                        }
                                    });
                                    menu.show();
                                } else {
                                    menu.setPositiveButton("사용", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //아이디 인풋창 고정 후 중복체크 변수 변경
                                            idCheckResult = true;
                                            input_id.setEnabled(false);
                                            id_check.setVisibility(view.GONE);
                                            dialog.dismiss(); //다이얼로그 제거
                                        }
                                    });
                                    menu.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss(); //다이얼로그 제거
                                        }
                                    });
                                    menu.show();
                                }

                            } else {
                                Toast.makeText(signUp.this, "서버 통신 실패", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<IDCheckResponse> call, Throwable t) {

                            Log.d("TAG", "onFailure: ID 중복체크 통신 실패");

                        }
                    });

                } else {
                    Toast.makeText(signUp.this, "아이디가 공백입니다", Toast.LENGTH_SHORT).show();
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

                    //온리스타트에서 시간 서버랑 재통신해서 남은시간 가져와야함
                    //온 리스타트에서 타이머러닝이 트루면 서버와 재통신해서 남은시간 가져와서 타이머 재실행
//                    SMS_timer = new SMS_timer(getMainLooper(), 0, 5, count);
//                    SMS_timer.startTimer();

                    //서버와 통신
                    SMSVerifi(new SMSVerifiData(phone_num,"singUp"), phone_num, count);
                }
            }
        });
        //인증버튼 클릭 시
        certification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_num = phone_number_input.getText().toString(); //휴대폰번호
                String certificationNum = certification_input.getText().toString(); //인증번호 입력칸

                if(certificationNum.equals("")){ //인증번호 빈칸일시
                    Toast.makeText(signUp.this, "인증 번호를 입력 해주세요!", Toast.LENGTH_SHORT).show();
                }else{
                    //서버와 통신해서 인증번호 검증
                    service.certificationCheck(certificationNum,phone_num).enqueue(new Callback<certificationCheckResponse>() {
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
                                    Toast.makeText(signUp.this, message, Toast.LENGTH_SHORT).show();
                                }else if (code == 1){
                                    //인증번호 틀렸다는 메시지
                                    Toast.makeText(signUp.this, message, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(signUp.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(signUp.this, "서버 통신 실패", Toast.LENGTH_SHORT).show();
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
        signUp_btn.setOnClickListener(new View.OnClickListener() { //회원가입 버튼
            @Override
            public void onClick(View v) {
                //변수 체크하고 정보받아서 디비에 저장
                //모든 정보가 작성된상태일때
                if(idCheckResult == true && psCheck == true && !(nick_input.getText().toString().equals("")) && certificationCheck == true){
                    String userId = input_id.getText().toString();
                    String userPs = input_ps.getText().toString();
                    String userNick = nick_input.getText().toString();
                    String userPhoneNum = phone_number_input.getText().toString();

                    service.register(new registerData(userId,userPs,userNick,userPhoneNum)).enqueue(new Callback<registerResponse>() {
                        @Override
                        public void onResponse(Call<registerResponse> call, Response<registerResponse> response) {
                            registerResponse registerResponse = response.body();
                            String message = registerResponse.getMessage(); //반환 메시지

                            Toast.makeText(signUp.this, message, Toast.LENGTH_SHORT).show(); //회원가입이 완료되었습니다

                            Intent intent = new Intent(getApplicationContext(), login.class);
                            startActivity(intent); //로그인 화면으로 이동
                            finish();	//현재 액티비티 종료
                        }

                        @Override
                        public void onFailure(Call<registerResponse> call, Throwable t) {

                        }
                    });

                }else{
                    Toast.makeText(signUp.this, "회원 정보를 작성 해주세요", Toast.LENGTH_SHORT).show();
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

    private void SMSVerifi(SMSVerifiData data, String phoneNum, TextView countText) {
        service.SMSVerifi(data).enqueue(new Callback<SMSVerifiResponse>() {
            @Override
            public void onResponse(Call<SMSVerifiResponse> call, Response<SMSVerifiResponse> response) {
                SMSVerifiResponse result = response.body();

                //카운트다운 시작
                //처음엔 카운트가 5 : 00 임 구분은 timerRunning변수로 첫실행인지 홈화면 갔다가 온건지 구분

                if (result.getMessage().equals("false")) { //인증정보가 이미 존재 할 경우

                    Toast.makeText(signUp.this, "조금 뒤에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                }else if(result.getMessage().equals("fail")){
                    Toast.makeText(signUp.this, "이미 존재하는 아이디 입니다", Toast.LENGTH_SHORT).show();
                }else {
                    //인증확인 버튼
                    Button certification_btn = (Button) signUp.this.findViewById(R.id.certification_btn);
                    //핸드폰 번호 입력칸
                    EditText phone_number_input = findViewById(R.id.phone_number_input);
                    //인증번호 입력칸
                    EditText certification_input = findViewById(R.id.certification_input);
                    //인증버튼 활성화
                    certification_btn.setEnabled(true);

                    phone_number_input.setEnabled(false); //휴대폰번호 입력란 비활성화
                    certification_input.setEnabled(true); //인증번호 입력란 활성화
                    //인증번호 발송
                    sendSMS.send(phoneNum, result.getMessage(), signUp.this, signUp.this.getClass());
                    int count = Integer.parseInt(result.getCount());
                    //타이머 스타트
                    SMS_timer = new SMS_timer(getMainLooper(), 0, count, countText,certification_btn);
                    SMS_timer.startTimer();
                    //인증코드
                    String code = result.getMessage(); //인증번호
                    //타이머 재시작 생명주기에서 만져줘야함

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

}

//일단은 카운트다운 기능까지만 구현하고 내일 서버와 통신해서 시간계산하는기능 넣어보기