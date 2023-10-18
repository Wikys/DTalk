package com.example.dtalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    //구글 계정
    private GoogleSignInAccount gsa;

    // 구글api클라이언트
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;



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
        SignInButton google_sns_login_btn = (SignInButton) login.this.findViewById(R.id.google_sns_login_btn);
        google_sns_login_btn.setOnClickListener(new View.OnClickListener() {
            //구글 로그인 기능
            @Override
            public void onClick(View view) {
            // 기존에 로그인 했던 계정을 확인한다.
                gsa = GoogleSignIn.getLastSignedInAccount(login.this);

                if (gsa != null) // 로그인 되있는 경우
                    Toast.makeText(login.this, "로그인이 되어있네요?", Toast.LENGTH_SHORT).show();
                else
                    signIn();
            }

        });
        Button google_sns_logout_btn = (Button) login.this.findViewById(R.id.google_sns_logout_btn);
        google_sns_logout_btn.setOnClickListener(view -> {
            signOut(); //로그아웃
        });


    }
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    /* 사용자 정보 가져오기 */
    //여기 정보들을 디비에 입력
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                firebaseAuthWithGoogle(acct.getIdToken());

                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d("TAG", "handleSignInResult:personName "+personName);
                Log.d("TAG", "handleSignInResult:personGivenName "+personGivenName);
                Log.d("TAG", "handleSignInResult:personEmail "+personEmail);
                Log.d("TAG", "handleSignInResult:personId "+personId);
                Log.d("TAG", "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d("TAG", "handleSignInResult:personPhoto "+personPhoto);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("TAG", "signInResult:failed code=" + e.getStatusCode() + ", message=" + e.getMessage());
        }
    }
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        Toast.makeText(login.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(login.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {

    }

    /* 로그아웃 */ // 나중에 설정에서 활용
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    mAuth.signOut();
                    Toast.makeText(login.this, R.string.success_logout, Toast.LENGTH_SHORT).show();
                    // ...
                });
        gsa = null;
    }

    /* 회원 삭제요청 */ //나중에 설정에서 활용
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {
                    // ...
                });
    }



}