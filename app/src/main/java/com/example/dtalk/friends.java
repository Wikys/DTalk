package com.example.dtalk;

import static com.example.dtalk.retrofit.RetrofitClient.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dtalk.JWTHelper.JWTHelper;
import com.example.dtalk.recyclerview.friendsAdapter;
import com.example.dtalk.retrofit.RetrofitClient;
import com.example.dtalk.retrofit.ServerApi;
import com.example.dtalk.retrofit.friendsListCheckResponse;
import com.example.dtalk.retrofit.userInformationSearchResponse;
import com.example.dtalk.searchFriend.add_friend;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
    private RecyclerView friendList_R;
    private TextView number_of_friends;
    private ImageButton search_friend;
    private EditText search_friend_input;
    private Boolean showSearchBar;
    private ArrayList<friendsListCheckResponse.Friend> originData;
    private ArrayList<friendsListCheckResponse.Friend> friendsList;
    private friendsAdapter friendsAdapter;
    private View my_profile;
    String myId;



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
        friendList_R = view.findViewById(R.id.friends_profile); //친구목록 리사이클러뷰
        number_of_friends = view.findViewById(R.id.number_of_friends); //친구수
        search_friend = view.findViewById(R.id.search_friend); //친구검색 버튼
        search_friend_input = view.findViewById(R.id.search_friend_input);//친구 검색인풋
        showSearchBar = false; // 친구검색바
        my_profile = view.findViewById(R.id.my_profile);//내프로필 버튼


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
                        myId = userId; //내아이디 맴버변수에 저장
                        Log.d("TAG", "상태메시지: "+statusMsg +" 닉네임: "+userNick);

                        my_profile_nick.setText(userNick); //닉네임 메시지 변경
                        my_porfile_msg.setText(statusMsg); //상태 메시지 변경
                        //이미지 로직 만들고 추가해야함
                        // Glide를 사용하여 이미지 로딩
                        Glide.with(getActivity())
                                .load(BASE_URL+result.getUserProfileImg()) // friend.getImageUrl()는 이미지의 URL 주소
                                .into(my_profile_img);
                        Log.d("TAG", "onResponse: "+result.getUserProfileImg());

                    }

                    @Override
                    public void onFailure(Call<userInformationSearchResponse> call, Throwable t) {

                    }
                });

                //친구목록 불러오기
                //친구수도 변경해줘야함
                service.friendsListCheck(userId).enqueue(new Callback<friendsListCheckResponse>() {
                    @Override
                    public void onResponse(Call<friendsListCheckResponse> call, Response<friendsListCheckResponse> response) {
                        friendsListCheckResponse result = response.body();
                        originData = new ArrayList<>(result.getFriends());
                        friendsList = new ArrayList<>();

                        if(result.getStatus().equals("success")){//친구 목록 불러왔을때
                            friendsAdapter = new friendsAdapter(originData,getActivity()); //어댑터
                            friendList_R.setLayoutManager(new LinearLayoutManager(getActivity())); // 레이아웃 매니저 설정 (리스트 형태)
                            friendList_R.setAdapter(friendsAdapter);
                            number_of_friends.setText("친구 "+Integer.toString(originData.size()));


                        } else if (result.getStatus().equals("N/A")) { //친구가 없을때
                            //친구 0명인것만 표기
                            number_of_friends.setText("친구 "+Integer.toString(originData.size()));
                        }

                    }

                    @Override
                    public void onFailure(Call<friendsListCheckResponse> call, Throwable t) {

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
        my_profile.setOnClickListener(new View.OnClickListener() { //내프로필 터치시
            @Override
            public void onClick(View v) {
                //내 프로필 화면으로 이동
                Intent intent = new Intent(getActivity(), com.example.dtalk.my_profile.class);
                intent.putExtra("userId",myId);
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

        search_friend.setOnClickListener(new View.OnClickListener() { //친구검색 버튼 클릭시
            @Override
            public void onClick(View v) {
                if(showSearchBar == false){//서치바가 꺼져있을때
                    //누르면 검색창 보이게하기
                    search_friend_input.setVisibility(View.VISIBLE);
                    showSearchBar = true;
                }else{
                    search_friend_input.setVisibility(View.GONE);
                    search_friend_input.setText(""); //텍스트 비워주고
                    showSearchBar = false;
                }

            }
        });

        search_friend_input.addTextChangedListener(new TextWatcher() { //친구 검색창에 스트링값 들어오면 결과변경
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = search_friend_input.getText().toString(); //검색창에 들어온값을 스트링으로 형변환
                searchFilter(searchText);

            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void searchFilter(String searchText) {

        friendsList.clear(); //필터 일단 싹비워주고(전에 검색했던기록 안나오게)

        for (int i = 0; i < originData.size(); i++) {
            //아이템 전체목록 싹 훑어주고
            if (originData.get(i).getUserName().toLowerCase().contains(searchText.toLowerCase())) {

//                검색창에 써진 텍스트를 포함하는 아이템을 찾아서 다시담아줌
                friendsList.add(originData.get(i));
                //아이템필터에 추가
            }
        }
        friendsAdapter.itemfilter(friendsList); // 기존 어레이리스트 교체
    }
}