package com.example.dtalk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class signUp extends AppCompatActivity {

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


    }

//    private void sendSMS(String phoneNumber, String message){
//        PendingIntent pi = PendingIntent.getActivity(this,0,
//                new Intent(this,signUp.class), PendingIntent.FLAG_IMMUTABLE);
//        SmsManager sms = SmsManager.getDefaultSmsSubscriptionId();
//
//    }
}