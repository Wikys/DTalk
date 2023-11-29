package com.example.dtalk.searchFriend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtalk.R;
import com.example.dtalk.login;
import com.example.dtalk.retrofit.JWTCheckResponse;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.addFriendData;
import com.example.dtalk.retrofit.addFriendResponse;
import com.example.dtalk.retrofit.addFriendSearchResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class add_friend extends AppCompatActivity {
    EditText input_friend_id;
    Button search_friend;
    private ServerApi service;
    private SharedPreferences preferences;
    private String userId;
    private View search_result;
    private ImageView my_profile_img;
    private TextView my_profile_nick;
    private TextView my_porfile_msg;
    private ImageButton add_friend_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //jwt (MODE_PRIVATE (이 앱에서만 사용가능))
        preferences = getSharedPreferences("JWT", MODE_PRIVATE);
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
                        Toast.makeText(add_friend.this, "엑세스 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                    } else if (JWTCheckResponse.getStatus().equals("hacked")) { //비정상적인 접근시
                        Toast.makeText(add_friend.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        //액티비티 테스크를 전부 지우고 로그인화면으로 이동
                        Intent intent = new Intent(add_friend.this, login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //기존 플래그 삭제하고 새로운 플래그달음
                        startActivity(intent);
                    } else if (JWTCheckResponse.getStatus().equals("error")) { //에러시
                        Toast.makeText(add_friend.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (JWTCheckResponse.getStatus().equals("reissued")) { //엑세스토큰 만료되서 재발급시
                        String JWT = JWTCheckResponse.getAccessToken();//JWT
                        //쉐어드에 저장
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("JWT", JWT);
                        editor.commit();

                        Toast.makeText(add_friend.this, "리프레시 토큰 유효", Toast.LENGTH_SHORT).show();
                        userId = JWTCheckResponse.getUserId(); //유저아이디 넣기

                        Toast.makeText(add_friend.this, "엑세스토큰 만료되서 재발급", Toast.LENGTH_SHORT).show();

                    } else if (JWTCheckResponse.getStatus().equals("expired")) { //리프레시토큰 만료시
                        Toast.makeText(add_friend.this, JWTCheckResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Log.d("TAG", "JWT토큰 onResponse: " + JWTCheckResponse.getUserId());


                }

                @Override
                public void onFailure(Call<JWTCheckResponse> call, Throwable t) {

                }
            });
        }

        input_friend_id = add_friend.this.findViewById(R.id.input_friend_id); //아이디 입력칸
        search_friend = add_friend.this.findViewById(R.id.search_friend); //아이디 검색 버튼
        search_result = add_friend.this.findViewById(R.id.search_result); //검색후 친구프로필


        search_friend.setOnClickListener(new View.OnClickListener() { //검색버튼 클릭시
            @Override
            public void onClick(View v) {
                String friendId = input_friend_id.getText().toString(); //친구 아이디 입력칸에 써져있는 텍스트

                if(userId.equals(friendId)){ //자기아이디를 검색했을때
                    Toast.makeText(add_friend.this, "나 자신은 검색할 수 없습니다", Toast.LENGTH_SHORT).show();
                }else{ //타인의 아이디를 검색했을때
                    service.addFriendSearch(friendId).enqueue(new Callback<addFriendSearchResponse>() {
                        @Override
                        public void onResponse(Call<addFriendSearchResponse> call, Response<addFriendSearchResponse> response) {
                            addFriendSearchResponse addFriendSearchResponse = response.body();


                            if (addFriendSearchResponse.getSearchResult() == true){ //검색결과가 있을때
                                search_result.setVisibility(View.VISIBLE);
                                //나중에 이미지 추가하자
                                my_profile_nick = add_friend.this.findViewById(R.id.my_profile_nick); //닉네임
                                my_porfile_msg = add_friend.this.findViewById(R.id.my_porfile_msg);//상태메시지
                                add_friend_btn = add_friend.this.findViewById(R.id.add_friend_btn); //친구추가 버튼

                                //유저아이디 여기에있으니까 그거쓰면될듯
                                String userNick = addFriendSearchResponse.getUserNick();
                                String userStatusMsg = addFriendSearchResponse.getUserStatusMsg();
                                my_profile_nick.setText(userNick.toString());
                                my_porfile_msg.setText(userStatusMsg.toString());

                                //친구추가 버튼을 누를시 해당 친구와 친구관계를 만들어야함
                                //그리고 이미 친구관계인 친구는 친구추가시 이미 친구입니다라고 뜨게해야함
                                add_friend_btn.setOnClickListener(new View.OnClickListener() { //친구추가버튼 클릭시
                                    @Override
                                    public void onClick(View v) {
                                        service.addFriend(new addFriendData(userId,friendId)).enqueue(new Callback<addFriendResponse>() {
                                            @Override
                                            public void onResponse(Call<addFriendResponse> call, Response<addFriendResponse> response) {
                                                addFriendResponse addFriendResponse = response.body();
                                                if(addFriendResponse.getStatus().equals("success")){//친구추가 완료시
                                                    Toast.makeText(add_friend.this, addFriendResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                                } else if (addFriendResponse.getStatus().equals("duplication")) { //이미 친구상태일때
                                                    Toast.makeText(add_friend.this, addFriendResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                    
                                                } else if (addFriendResponse.getStatus().equals("error")) { //데이터가 안넘어왔을때
                                                    Toast.makeText(add_friend.this, addFriendResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<addFriendResponse> call, Throwable t) {

                                            }
                                        });
                                    }
                                });



                            }else{ //검색결과가 없을때
                                search_result.setVisibility(View.GONE);
                                Toast.makeText(add_friend.this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<addFriendSearchResponse> call, Throwable t) {

                        }
                    });
                }

            }
        });




    }
}