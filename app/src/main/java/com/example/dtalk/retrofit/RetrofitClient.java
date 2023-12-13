package com.example.dtalk.retrofit;

import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public final static String BASE_URL = "http://13.239.32.202:80/";
    private static Retrofit retrofit = null;

    private SharedPreferences preferences;


    // Retrofit 객체 초기화
    private RetrofitClient() {
    }

    public static Retrofit getClient(SharedPreferences preferences) {
        //쉐어드에서 JWT 가져오기
        String authToken = preferences.getString("JWT", ""); // 토큰값 가져오기 없으면 ""

        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException { //헤더에 엑세스토큰 추가
                            Request originalRequest = chain.request();
                            Request newRequest;
                            // 토큰이 비어 있지 않은 경우에만 헤더 추가
                            if (!authToken.equals("")) {
                                newRequest = originalRequest.newBuilder()
                                        .addHeader("Authorization", authToken)
//                                "Bearer " +
                                        .build();
                            } else {
                                newRequest = originalRequest;
                            }

                            return chain.proceed(newRequest);
                        }
                    }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // 요청을 보낼 base url을 설정한다.
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱을 위한 GsonConverterFactory를 추가한다.
                    .build();
        }

        return retrofit;
    }
}
