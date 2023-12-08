package com.example.dtalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dtalk.JWTHelper.JWTHelper;
import com.example.dtalk.retrofit.JWTCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.logoutResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class settings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button logout_btn;
    private SharedPreferences preferences;
    private ServerApi service;

    private Boolean JWTLoading = false;

    public settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment setting.
     */
    // TODO: Rename and change types and number of parameters
    public static settings newInstance(String param1, String param2) {
        settings fragment = new settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getActivity().getSharedPreferences("JWT", Context.MODE_PRIVATE);
        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);


            logout_btn = view.findViewById(R.id.logout_btn); //로그아웃 버튼
            logout_btn.setOnClickListener(new View.OnClickListener() { //로그아웃버튼 클릭시
                @Override
                public void onClick(View v) {

                    JWTHelper JWTHelper = new JWTHelper(getActivity());

                    JWTHelper.checkJWTAndPerformAction(new JWTHelper.JwtCheckCallback() {
                        @Override
                        public void onJwtValid(String userId, String message) {
                            //토큰 존재시

                            //디비에 저장된 리프레시토큰 삭제
                            service.logout(userId).enqueue(new Callback<logoutResponse>() {
                                @Override
                                public void onResponse(Call<logoutResponse> call, Response<logoutResponse> response) {
                                    logoutResponse logoutResponse = response.body();
                                    Log.d("TAG", "onResponse: 눌렸음");
                                    if (logoutResponse.getStatus().equals("success")){
                                        //정상적인 로그아웃일시
                                        Toast.makeText(getActivity(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.remove("JWT"); //로컬에 저장된 JWT삭제
                                        editor.apply(); //커밋

                                        Intent intent = new Intent(getActivity(), login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                                        startActivity(intent);
                                        //로그인화면 이동

                                    } else if (logoutResponse.getStatus().equals("hacked")) {
                                        //비정상적인 접근일시
                                        Toast.makeText(getActivity(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.remove("JWT"); //로컬에 저장된 JWT삭제
                                        editor.apply(); //커밋
                                        Intent intent = new Intent(getActivity(), login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                                        startActivity(intent);
                                        //동작은 똑같이 로그인화면으로 보냄

                                    }
                                    //로그아웃 성공했다는 메시지 출력
//                                    Toast.makeText(getActivity(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<logoutResponse> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onJwtInvalid(String message) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    });






                }
            });

            return view;
        }
    }