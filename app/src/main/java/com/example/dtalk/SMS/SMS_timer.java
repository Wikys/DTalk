package com.example.dtalk.SMS;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class SMS_timer extends Handler {
    int min;
    int sec;
    TextView countText;

    public static Boolean timerRunning = false;

    public SMS_timer(@NonNull Looper looper, int min, int sec, TextView countText) {
        super(looper);
        this.min = min;
        this.sec = sec;
        this.countText = countText;
    }

    public void startTimer() {
        if (!timerRunning) {
            timerRunning = true;
            countText.setVisibility(View.VISIBLE);
            countText.setText(min + "분 " + sec + "초");
            Log.d("TAG", "startTimer: 타이머 시작");
            sendEmptyMessage(0);
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (timerRunning) {
            switch (msg.what) {
                case 0: // 타이머 시작
                    if (sec > 0 || min > 0) {
                        sec--;
                        countText.setText(min + "분 " + sec + "초");
                        Log.d("TAG", "handleMessage: "+min + "분 " + sec + "초");
                        sendEmptyMessageDelayed(0, 1000);

                        if (sec == 0 && min > 0) {
                            sec = 59; // 초를 다시 59로 설정
                            min--;
                        }
                    } else {
                        timerRunning = false;
                        countText.setVisibility(View.INVISIBLE);
                        Log.d("TAG", "handleMessage: 타이머 종료");
                    }
                    break;
            }
        }
    }
}
