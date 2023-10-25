package com.example.dtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dtalk.register.signUp;

public class tos extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tos);
        //이용약관 동의 체크박스
        CheckBox agree_TOS_checkBox = (CheckBox) findViewById(R.id.TOS_agree) ;

        Button next_btn = (Button) tos.this.findViewById(R.id.next_btn);
        //다음 버튼
        next_btn.setOnClickListener(new View.OnClickListener() {
            //회원가입 화면 이동
            @Override
            public void onClick(View view) {
                //이용약관 체크박스를 누르고 다음 버튼을 눌렀을때
                if (agree_TOS_checkBox.isChecked()){
                    Intent intent = new Intent(tos.this, signUp.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(tos.this, "이용약관에 동의하셔야 회원가입 진행이 가능합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}