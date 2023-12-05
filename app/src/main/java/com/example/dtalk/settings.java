package com.example.dtalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
    String userId;

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

        //쉐어드에서 JWT 가져오기
        String Token = preferences.getString("JWT", ""); // 토큰값 가져오기 없으면 ""
        if (!(Token.equals(""))) { //토큰이 존재하면
            //JWT 검증
            service.JWTCheck().enqueue(new Callback<JWTCheckResponse>() {
                @Override
                public void onResponse(Call<JWTCheckResponse> call, Response<JWTCheckResponse> response) {
                    JWTCheckResponse JWTCheckResponse = response.body();

                    //엑세스토큰이 존재하고 유효할경우 혹은 만료되서 리프레시 토큰으로 재발급 받았을경우
                    if (JWTCheckResponse.getStatus().equals("certification_valid")) {
                        Toast.makeText(getActivity(), "리프레시토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기
                        JWTLoading = true;

                    } else if (JWTCheckResponse.getStatus().equals("hacked")) { //비정상적인 접근시
                        //비정상적인 접근이라고 메시지 발송
                        Toast.makeText(getActivity(), JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        //액티비티 테스크를 전부 지우고 로그인화면으로 이동
                        Intent intent = new Intent(getActivity(), login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                        startActivity(intent);
                    } else if (JWTCheckResponse.getStatus().equals("error")) { //에러시
                        Toast.makeText(getActivity(), JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (JWTCheckResponse.getStatus().equals("reissued")) { //엑세스토큰 만료되서 재발급시
                        String JWT = JWTCheckResponse.getAccessToken();//JWT
                        //쉐어드에 저장
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("JWT", JWT);
                        editor.commit();

                        Toast.makeText(getActivity(), "리프레시토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기
                        JWTLoading = true;

                        Toast.makeText(getActivity(), "엑세스토큰 만료되서 재발급", Toast.LENGTH_SHORT).show();

                    } else if (JWTCheckResponse.getStatus().equals("expired")) { //리프레시토큰 만료시
                        Toast.makeText(getActivity(), JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JWTCheckResponse> call, Throwable t) {

                }
            });
        }

            logout_btn = view.findViewById(R.id.logout_btn); //로그아웃 버튼
            logout_btn.setOnClickListener(new View.OnClickListener() { //로그아웃버튼 클릭시
                @Override
                public void onClick(View v) {
                    if (JWTLoading == true){ //JWT 토큰 체크가 끝난상태일떄 작동
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("JWT"); //로컬에 저장된 JWT삭제
                        editor.commit(); //커밋
                        //디비에 저장된 리프레시토큰 삭제
                        service.logout(userId).enqueue(new Callback<logoutResponse>() {
                            @Override
                            public void onResponse(Call<logoutResponse> call, Response<logoutResponse> response) {
                                logoutResponse logoutResponse = response.body();
                                if (logoutResponse.getMessage().equals("success")){
                                    //정상적인 로그아웃일시
                                    Toast.makeText(getActivity(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), login.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                                    startActivity(intent);
                                    //로그인화면 이동

                                } else if (logoutResponse.getMessage().equals("hacked")) {
                                    //비정상적인 접근일시
                                    Toast.makeText(getActivity(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), login.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                                    startActivity(intent);
                                    //동작은 똑같이 로그인화면으로 보냄

                                }
                                //로그아웃 성공했다는 메시지 출력
                                Toast.makeText(getActivity(), logoutResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<logoutResponse> call, Throwable t) {

                            }
                        });
                    }else{
                        Toast.makeText(getActivity(), "잠시 뒤에 시도 해 주세요.", Toast.LENGTH_SHORT).show();
                    }



                }
            });

            return view;
        }
    }