package com.example.detectionofmobilepay;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MyNotificationListener extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pkg=sbn.getPackageName();
        String title="";
        String content="";
        Notification notification = sbn.getNotification();
        if (notification == null) {
            return;
        }
        Bundle extras = notification.extras;
        if (extras != null) {
            // 获取通知标题
            title = extras.getString(Notification.EXTRA_TITLE, "");
            // 获取通知内容
            content = extras.getString(Notification.EXTRA_TEXT, "");
            if (!TextUtils.isEmpty(title) && title.contains("订阅"))
            {
                //Toast.makeText(getApplicationContext(), "检测到订阅", Toast.LENGTH_SHORT).show();
            }
            else if (!TextUtils.isEmpty(title) && content.contains("吃什么"))
            {
                //Toast.makeText(getApplicationContext(), "检测到聊天消息", Toast.LENGTH_SHORT).show();
            }
        }
        String[] nos = {pkg,title,content};
        Intent intent = new Intent("android.service.notification.NotificationListenerService");
        intent.putExtra("nos",nos);
        sendBroadcast(intent);
    }


}