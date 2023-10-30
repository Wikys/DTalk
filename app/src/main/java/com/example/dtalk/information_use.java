package com.example.dtalk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class information_use extends AppCompatActivity {
    private permission_support permission;
    private Activity activity;
    private Context context;

    private static final int PERMISSIONS_REQUEST_CODE = 1023;

//        private String[] permissions = {
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.READ_MEDIA_IMAGES,
//            Manifest.permission.READ_MEDIA_VIDEO,
//            Manifest.permission.READ_MEDIA_AUDIO
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_use);


//        activity = this;
//        permission = new permission_support(this, this);
//
        //퍼미션 체크하고 돼있으면 바로 다음화면 안돼있으면 화면그냥 출력
        permissionCheck();

        Button allow_btn = (Button) information_use.this.findViewById(R.id.allow_btn);
        //허용하기 버튼
        allow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionListener permissionListener = new PermissionListener() {
                    //권한 존재하면 넘어가는 메소드
                    @Override
                    public void onPermissionGranted() {
                        Toast.makeText(information_use.this, "권한이 허가된 상태입니다.", Toast.LENGTH_SHORT).show();
                        Log.e("권한", "권한 허가 상태");
                        Intent intent = new Intent(information_use.this, login.class);
                        startActivity(intent);
                    }

                    //권한이 없으면 실행되는 메소드
                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(information_use.this, "권한을 허용하셔야 앱을 이용 하실수 있습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("권한", "권한 거부 상태");
                    }
                };
                TedPermission.create()
                        .setPermissionListener(permissionListener)
                        .setRationaleMessage("권한 허용이 필요합니다.")
                        .setDeniedMessage("권한 허용 거부 시 진행 불가 [설정] > [권한] 에서 권한을 허용 가능합니다.")
                        .setPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
                        .check();
            }

        });
    }

    // 권한 체크
    private void permissionCheck() {

        // SDK 23버전 이하 버전에서는 Permission이 필요하지 않음
        if (Build.VERSION.SDK_INT >= 23) {
            // 방금 전 만들었던 클래스 객체 생성
            permission = new permission_support(this, this);

            // 권한 체크한 후에 리턴이 true로 들어오면 로그인 화면 진입
            if (permission.checkPermission()) {
                Intent intent = new Intent(information_use.this, login.class);
                startActivity(intent);
                Log.d("권한요청 확인", "permissionCheck: 권한요청 확인 로그인화면 이동");
            }

        }
    }
}