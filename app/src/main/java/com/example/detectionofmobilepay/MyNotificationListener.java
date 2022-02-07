package com.example.detectionofmobilepay;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;


public class MyNotificationListener extends NotificationListenerService {
    private String getMoneyCount(String string)
    {
        char[] m =new char[10];
        string.getChars(string.lastIndexOf("款")+1,string.lastIndexOf("元"),m,0);
        String str=new String(m);
        return str;
    }
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
            Intent intent = new Intent("android.service.notification.NotificationListenerService");
            if (pkg.equals("com.tencent.mm")&&title.contains("微信支付")&&content.contains("微信支付收款"))
            {
                String[] nos={pkg,getMoneyCount(content)};
                intent.putExtra("nos",nos);
                sendBroadcast(intent);
            }
            else if (pkg.equals("com.eg.android.AlipayGphone")&&title.contains("收款通知")&&content.contains("通过扫码"))
            {
                String[] nos={pkg,getMoneyCount(content)};
                intent.putExtra("nos",nos);
                sendBroadcast(intent);
            }
            else if(title.contains("test")&&content.contains("this is a test"))
            {
                String[] toast={"nos","getit"};
                intent.putExtra("nos",toast);
                sendBroadcast(intent);
            }
        }
    }


}