package com.example.dtalk;

//import static com.example.dtalk.retrofit.RetrofitClient.myServerUrl;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dtalk.retrofit.GJoinData;
import com.example.dtalk.retrofit.GJoinResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    //구글 계정
    private GoogleSignInAccount gsa;
    private ServerApi service;

    // 구글api클라이언트
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient().create(ServerApi.class);

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

                if (gsa != null) // 로그인 되있는 경우 다음화면으로 넘어간다
                    //테스트코드
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

    private void signIn() {
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

    /* 사용자 정보 가져오고 서버와 통신하여 디비에 입력 */
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

                Log.d("TAG", "handleSignInResult:personName " + personName);
//                Log.d("TAG", "handleSignInResult:personGivenName "+personGivenName);
                Log.d("TAG", "handleSignInResult:personEmail " + personEmail);
                Log.d("TAG", "handleSignInResult:personId " + personId);
//                Log.d("TAG", "handleSignInResult:personFamilyName "+personFamilyName);
//                Log.d("TAG", "handleSignInResult:personPhoto "+personPhoto);
                String username = personName;
                String email = personEmail;
                String password = personId;
//                Log.d("확인용", "handleSignInResult:  "+username+","+email+","+password);

                Gjoin(new GJoinData(email,password,username));

                //구글 로그인 또는 구글 회원가입을 하면 로그인 토큰 발급후 다음화면으로 넘어감
                //()
                //서버에는 아직 로그인토큰 발급 로직이 없음 (추가예정)

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
//                        Toast.makeText(login.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        Toast.makeText(login.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
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
    private void Gjoin(GJoinData data){ //구글 회원가입시 디비에 등록하고 메세지를 받아오는 메소드 // 없애고 바꿔야할듯
        service.GUserJoin(data).enqueue(new Callback<GJoinResponse>() {
            @Override
            public void onResponse(Call<GJoinResponse> call, Response<GJoinResponse> response) {
                GJoinResponse result = response.body();
                Toast.makeText(login.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("회원가입 완료 확인용", "onResponse: "+result.getMessage());

            }
            @Override
            public void onFailure(Call<GJoinResponse> call, Throwable t) {
                Toast.makeText(login.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌

            }
        });
    }


}