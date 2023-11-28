package com.example.dtalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class navi extends AppCompatActivity {
    private friends fragment_friends; //친구
    private one_to_one_chat_list fragment_one_to_one_chat_list; //1:1
    private one_to_many_chat_list fragment_one_to_many_chat_list; //일대다
    private settings fragment_settings; //세팅
    private ImageButton friends_btn; //친구버튼
    private ImageButton one_to_one_chat_list_btn; //일대일채팅 버튼
    private ImageButton one_to_many_chat_list_btn; //일대다 버튼
    private ImageButton setting_btn; //세팅 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        fragment_friends = new friends(); //프렌즈 프래그먼트
        fragment_one_to_one_chat_list = new one_to_one_chat_list(); //1:다 채팅목록
        fragment_one_to_many_chat_list = new one_to_many_chat_list(); //일대다 채팅목록
        fragment_settings = new settings(); //설정목록

        //프래그먼트 매니저 획득
        FragmentManager fragmentManager = getSupportFragmentManager();

        //프래그먼트 Transaction 획득
        //프래그먼트를 올리거나 교체하는 작업을 Transaction이라고 함
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //프래그먼트를 FrameLayout의 자식으로 등록
        fragmentTransaction.add(R.id.main_frame, fragment_friends);
        //commit을 하면 자식으로 등록된 프래그먼트가 화면에 보임
        fragmentTransaction.commit();

        friends_btn = navi.this.findViewById(R.id.friends_btn);//친구버튼
        one_to_one_chat_list_btn = navi.this.findViewById(R.id.one_to_one_chat_btn);//일대일채팅 버튼
        one_to_many_chat_list_btn = navi.this.findViewById(R.id.one_to_many_chat_list_btn);//일대다 버튼
        setting_btn = navi.this.findViewById(R.id.setting_btn);//세팅 버튼

        friends_btn.setOnClickListener(v -> { //친구버튼 클릭시
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_frame, fragment_friends); //replace를 사용하면 프래그먼트를 원하는것으로 교체가능
            ft.commit();
        });
        one_to_one_chat_list_btn.setOnClickListener(v -> { //일대일채팅 버튼 클릭시
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_frame, fragment_one_to_one_chat_list); //replace를 사용하면 프래그먼트를 원하는것으로 교체가능
            ft.commit();
        });
        one_to_many_chat_list_btn.setOnClickListener(v -> { //일대다채팅 버튼 클릭시
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_frame, fragment_one_to_many_chat_list); //replace를 사용하면 프래그먼트를 원하는것으로 교체가능
            ft.commit();
        });
        setting_btn.setOnClickListener(v -> { //설정버튼 클릭시
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_frame, fragment_settings); //replace를 사용하면 프래그먼트를 원하는것으로 교체가능
            ft.commit();
        });



    }
}