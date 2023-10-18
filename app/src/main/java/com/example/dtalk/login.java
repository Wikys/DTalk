package com.example.dtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 파이어베이스 인증 객체 선언
        mAuth = FirebaseAuth.getInstance();

        // Google 로그인을 앱에 통합
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btnGoogleLogin = findViewById(R.id.btn_google_sign_in);

        btnGoogleLogin.setOnClickListener(view -> {
            // 기존에 로그인 했던 계정을 확인한다.
            gsa = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

            if (gsa != null) // 로그인 되있는 경우
                Toast.makeText(MainActivity.this, R.string.status_login, Toast.LENGTH_SHORT).show();
            else
                signIn();
        });

        btnLogoutGoogle = findViewById(R.id.btn_logout_google);
        btnLogoutGoogle.setOnClickListener(view -> {
            signOut(); //로그아웃
        });



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