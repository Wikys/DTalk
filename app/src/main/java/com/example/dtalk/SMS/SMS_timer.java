package com.example.dtalk.SMS;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SMS_timer {
    Handler SMSHandler;
    private static Timer m_timer;
    private static TimerTask mt_timer;
    int min;
    int sec;
    Handler handler;
    Activity ac;

    public static Boolean timerRunning = false;


    public void timer(TextView countText) { //타이머 생성 메소드

        if (m_timer == null || mt_timer == null) {
            handler = new Handler(Looper.getMainLooper());
            timerRunning = true;
            min = 0;
            sec = 5;
            countText.setVisibility(View.VISIBLE); //카운트다운 보이게하기

            m_timer = new Timer();
            mt_timer = new TimerTask(){
                @Override
                public void run() {


                    if(sec > 0 || min > 0){
                        //타이머 작동중
                        sec--;

                        if(sec == 0 && min > 0){
                            sec = 59; //초를 다시 59로 설정
                            min--;
                        }
                        handler.post(new Runnable() { //핸들러
                            @Override
                            public void run() {
                                //타이머 업데이트
                                countText.setText(min+"분 "+sec+"초");
                            }
                        });

                    }else {
                        //타이머 종료
                        m_timer.cancel();
                        mt_timer.cancel();
                        timerRunning = false;
                        countText.setVisibility(View.INVISIBLE); //카운트다운 텍스트 가리기
                        m_timer = null;
                        mt_timer = null;

                    }
                }
            };
            m_timer.schedule(mt_timer,0,1000);
        }

    }
    public void timer_stop(TextView countText){
        //타이머 종료
        m_timer.cancel();
        mt_timer.cancel();
        timerRunning = false;
        countText.setVisibility(View.INVISIBLE); //카운트다운 텍스트 가리기
        m_timer = null;
        mt_timer = null;
    }
}
