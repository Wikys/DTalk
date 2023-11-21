package com.example.dtalk.SMS;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class SMS_timer extends Handler {
    int min;
    int sec;
    TextView countText;
    Button certification_btn;

    public static Boolean timerRunning = false;

    public SMS_timer(@NonNull Looper looper, int min, int sec, TextView countText, Button certification_btn) {
        super(looper);
        this.min = min;
        this.sec = sec;
        this.countText = countText;
        this.certification_btn = certification_btn;
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
                        //인증버튼 가림
                        certification_btn.setEnabled(false);
                        Log.d("TAG", "handleMessage: 타이머 종료");
                    }
                    break;
            }
        }
    }
    // 타이머를 정지하고 리소스를 정리하는 메소드
    public void stopTimer() {
        timerRunning = false; // 타이머 실행 상태를 false로 설정하여 타이머를 중지
        removeCallbacksAndMessages(null); // 모든 콜백 및 메시지 제거
        countText.setVisibility(View.INVISIBLE); // 타이머 텍스트를 화면에서 숨김
        Log.d("TAG", "stopTimer: 타이머 정지 및 리소스 정리 완료");
    }
}
