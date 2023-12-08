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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtalk.JWTHelper.JWTHelper;
import com.example.dtalk.retrofit.JWTCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.userInformationSearchResponse;
import com.example.dtalk.searchFriend.add_friend;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link friends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class friends extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton add_friend_btn;
    private ServerApi service;
    private SharedPreferences preferences;
    private ImageView my_profile_img;
    private TextView my_profile_nick;
    private TextView my_porfile_msg;
    private Boolean JWTLoading = false;


    public friends() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment friends.
     */
    // TODO: Rename and change types and number of parameters
    public static friends newInstance(String param1, String param2) {
        friends fragment = new friends();
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

        View view = inflater.inflate(R.layout.fragment_friends,container,false);

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getActivity().getSharedPreferences("JWT", Context.MODE_PRIVATE);
        //레트로핏 api 객체 생성
        service = RetrofitClient.getClient(preferences).create(ServerApi.class);
        my_profile_img = view.findViewById(R.id.my_profile_img); //유저 이미지
        my_profile_nick = view.findViewById(R.id.my_profile_nick);//유저 닉네임
        my_porfile_msg = view.findViewById(R.id.my_porfile_msg); //유저 상태 메시지


        //쉐어드에서 JWT 가져오기
//        String Token = preferences.getString("JWT", ""); // 토큰값 가져오기 없으면 ""
//        if (!(Token.equals(""))) { //토큰이 존재하면
//            //JWT 검증
//            service.JWTCheck().enqueue(new Callback<JWTCheckResponse>() {
//                @Override
//                public void onResponse(Call<JWTCheckResponse> call, Response<JWTCheckResponse> response) {
//                    JWTCheckResponse JWTCheckResponse = response.body();
//
//                    //엑세스토큰이 존재하고 유효할경우 혹은 만료되서 리프레시 토큰으로 재발급 받았을경우
//                    if (JWTCheckResponse.getStatus().equals("certification_valid")) {
//                        Toast.makeText(getActivity(), "리프레시토큰 유효", Toast.LENGTH_SHORT).show();
//                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기
//                        JWTLoading = true;
//
//                    } else if (JWTCheckResponse.getStatus().equals("hacked")) { //비정상적인 접근시
//                        //비정상적인 접근이라고 메시지 발송
//                        Toast.makeText(getActivity(), JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                        //액티비티 테스크를 전부 지우고 로그인화면으로 이동
//                        Intent intent = new Intent(getActivity(), login.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
//                        startActivity(intent);
//                    } else if (JWTCheckResponse.getStatus().equals("error")) { //에러시
//                        Toast.makeText(getActivity(), JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    } else if (JWTCheckResponse.getStatus().equals("reissued")) { //엑세스토큰 만료되서 재발급시
//                        String JWT = JWTCheckResponse.getAccessToken();//JWT
//                        //쉐어드에 저장
//                        SharedPreferences.Editor editor = preferences.edit();
//                        editor.putString("JWT", JWT);
//                        editor.commit();
//
//                        Toast.makeText(getActivity(), "리프레시토큰 유효", Toast.LENGTH_SHORT).show();
//                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기
//                        JWTLoading = true;
//
//                        Toast.makeText(getActivity(), "엑세스토큰 만료되서 재발급", Toast.LENGTH_SHORT).show();
//
//                    } else if (JWTCheckResponse.getStatus().equals("expired")) { //리프레시토큰 만료시
//                        Toast.makeText(getActivity(), JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                    if (JWTLoading == true){//jwt 로딩이 끝나면
//                        //사용자정보 불러와서 채워넣기
//
//                        service.userInformationSearch(userId,"myInfo").enqueue(new Callback<userInformationSearchResponse>() { //화면에 띄울 내정보 가져오기
//                            @Override
//                            public void onResponse(Call<userInformationSearchResponse> call, Response<userInformationSearchResponse> response) {
//                                userInformationSearchResponse result = response.body();
//
//                                String statusMsg = result.getUserStatusMsg();
//                                String userNick = result.getUserNick();
//                                Log.d("TAG", "상태메시지: "+statusMsg +" 닉네임: "+userNick);
//
//                                my_profile_nick.setText(userNick); //닉네임 메시지 변경
//                                my_porfile_msg.setText(statusMsg); //상태 메시지 변경
//                                //이미지 로직 만들고 추가해야함
//
//                            }
//
//                            @Override
//                            public void onFailure(Call<userInformationSearchResponse> call, Throwable t) {
//
//                            }
//                        });
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<JWTCheckResponse> call, Throwable t) {
//
//                }
//            });
//        }

        JWTHelper JWTHelper = new JWTHelper(getActivity());

        JWTHelper.checkJWTAndPerformAction(new JWTHelper.JwtCheckCallback() {
            @Override
            public void onJwtValid(String userId, String message) {
                //토큰 존재시
                service.userInformationSearch(userId,"myInfo").enqueue(new Callback<userInformationSearchResponse>() { //화면에 띄울 내정보 가져오기
                    @Override
                    public void onResponse(Call<userInformationSearchResponse> call, Response<userInformationSearchResponse> response) {
                        userInformationSearchResponse result = response.body();

                        String statusMsg = result.getUserStatusMsg();
                        String userNick = result.getUserNick();
                        Log.d("TAG", "상태메시지: "+statusMsg +" 닉네임: "+userNick);

                        my_profile_nick.setText(userNick); //닉네임 메시지 변경
                        my_porfile_msg.setText(statusMsg); //상태 메시지 변경
                        //이미지 로직 만들고 추가해야함

                    }

                    @Override
                    public void onFailure(Call<userInformationSearchResponse> call, Throwable t) {

                    }
                });

            }

            @Override
            public void onJwtInvalid(String message) { //어떤이유로든 JWT가 존재하지 않으면
                //경고메시지 출력
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                //액티비티 테스크를 전부 지우고 로그인화면으로 이동
                Intent intent = new Intent(getActivity(), login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                startActivity(intent);
            }
        });

        add_friend_btn = view.findViewById(R.id.add_friend_btn); //친구추가 버튼
        add_friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), add_friend.class);
                startActivity(intent);
            }
        });


        return view;
    }
}