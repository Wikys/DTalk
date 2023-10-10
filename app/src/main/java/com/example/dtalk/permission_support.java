package com.example.dtalk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class permission_support {
    private Context context;
    private Activity activity;

    // 요청할 권한을 배열로 저장

    private String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private List permissionList;


    private final int MULTIPLE_PERMISSIONS = 456;

    // 생성자에서 Activity와 Context를 파라미터로 받았습니다.
    public permission_support(Activity _activity, Context _context){
        this.activity = _activity;
        this.context = _context;
    }

    // 허용 받아야할 권한이 남았는지 체크
    public boolean checkPermission(){
        int result;
        permissionList = new ArrayList<>();

        // 위에서 배열로 선언한 권한 중 허용되지 않은 권한이 있는지 체크
        for(String pm : permissions){
            result = ContextCompat.checkSelfPermission(context, pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }

        if(!permissionList.isEmpty()){
            return false;
        }
        return true;
    }



}
