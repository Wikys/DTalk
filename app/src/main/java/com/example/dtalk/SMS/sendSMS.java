package com.example.dtalk.SMS;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.widget.Toast;

import com.example.dtalk.MainActivity;

//SMS 발송 기능
public class sendSMS {
    int defaultSmsSubscriptionId = SubscriptionManager.getDefaultSmsSubscriptionId();
    private void sendSMS(String phoneNumber, String message, Context context, Class<?> targetClass ){
        //클래스 리터럴을 얻기위해 Class<?>사용
        PendingIntent pi = PendingIntent.getActivity(context,0,
                new Intent(context, targetClass), PendingIntent.FLAG_UPDATE_CURRENT);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber,null,message,pi,null);
        Toast.makeText(context, "메세지가 전송 되었습니다", Toast.LENGTH_SHORT).show();

    }
}
