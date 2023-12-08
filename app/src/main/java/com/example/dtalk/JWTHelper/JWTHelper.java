package com.example.dtalk.JWTHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.dtalk.retrofit.JWTCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JWTHelper {

    private SharedPreferences preferences;
    private ServerApi service;
    private Context context;

    public JWTHelper(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("JWT", Context.MODE_PRIVATE);
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);
    }

    public void checkJWTAndPerformAction(final JwtCheckCallback callback){
        String token = preferences.getString("JWT", ""); //JWT 쉐어드에서 가져옴

        if(!token.isEmpty()){ //JWT가 존재할때
            service.JWTCheck().enqueue(new Callback<JWTCheckResponse>() {
                @Override
                public void onResponse(Call<JWTCheckResponse> call, Response<JWTCheckResponse> response) {
                    JWTCheckResponse jwtCheckResponse = response.body();
                    if (jwtCheckResponse != null) {
                        if (jwtCheckResponse.getStatus().equals("certification_valid")) { //토큰이 유효한경우
                            callback.onJwtValid(jwtCheckResponse.getUserId(),jwtCheckResponse.getMessage());
                        } else if (jwtCheckResponse.getStatus().equals("hacked")) { //엑세스 토큰 변조시
                            callback.onJwtInvalid(jwtCheckResponse.getMessage());
                        } else if (jwtCheckResponse.getStatus().equals("error")) { //에러시 서버에는 아직 미정의
                            callback.onJwtInvalid(jwtCheckResponse.getMessage());
                        } else if (jwtCheckResponse.getStatus().equals("reissued")) { //엑세스토큰 만료되서 재발급시
                            String jwt = jwtCheckResponse.getAccessToken();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("JWT", jwt);
                            editor.apply();
                            callback.onJwtValid(jwtCheckResponse.getUserId(),jwtCheckResponse.getMessage());
                        } else if (jwtCheckResponse.getStatus().equals("expired")) { //리프레시토큰 만료시 재 로그인 유도
                            callback.onJwtInvalid(jwtCheckResponse.getMessage());
                        }
                    } else {
                        callback.onJwtInvalid(jwtCheckResponse.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<JWTCheckResponse> call, Throwable t) {

                }
            });
        }
    }

    public interface JwtCheckCallback {
        void onJwtValid(String userId, String message); //jwt가 유효할때 동작
        void onJwtInvalid(String message); //jwt가 유효하지않을때 동작
    }

}
