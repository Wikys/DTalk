package com.example.dtalk.JWTHelper;

import android.content.Context;
import android.content.SharedPreferences;

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
        String token = preferences.getString("JWT", "");

        if(!token.isEmpty()){
            service.JWTCheck().enqueue(new Callback<JWTCheckResponse>() {
                @Override
                public void onResponse(Call<JWTCheckResponse> call, Response<JWTCheckResponse> response) {

                }

                @Override
                public void onFailure(Call<JWTCheckResponse> call, Throwable t) {

                }
            });
        }
    }

    public interface JwtCheckCallback {
        void onJwtValid(String userId);
        void onJwtInvalid();
    }

}
