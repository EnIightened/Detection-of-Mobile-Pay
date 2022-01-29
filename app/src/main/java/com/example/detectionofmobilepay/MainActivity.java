package com.example.detectionofmobilepay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private NosReceiver nosreceiver;

    private NotificationManager manager;
    public String isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(context.getPackageName())) {
            return "True";
        } else {
            return "False";}
    }
    public void openNotificationListenSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
        filter.addAction("android.service.notification.NotificationListenerService");
        nosreceiver = new NosReceiver();
        registerReceiver(nosreceiver, filter);// 注册Broadcast Receiver
        setContentView(R.layout.activity_main);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);//获取系统通知服务
        TextView t1 = findViewById(R.id.StateofService);
        t1.setText(isNotificationListenerEnabled(this));//状态指示器
        ///编写通知渠道
        String channelId = "send";
        String channelName = "聊天消息";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        createNotificationChannel(channelId, channelName, importance);
        channelId = "check";
        channelName = "订阅消息";
        importance = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannel(channelId, channelName, importance);
        ///设置按钮界面
        Button check = findViewById(R.id.check);
        check.setOnClickListener(this);
        Button send = findViewById(R.id.send);
        send.setOnClickListener(this);
    }
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(nosreceiver);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:    //发送通知
                Notification notification = new NotificationCompat.Builder(this, "send")
                        .setAutoCancel(true)
                        .setContentTitle("收到聊天消息")
                        .setContentText("今天晚上吃什么")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //设置红色
                        .setColor(Color.parseColor("#F00606"))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .build();
                manager.notify(1, notification);
                break;
            case R.id.check:   //查收通知
                Notification notificationcheck = new NotificationCompat.Builder(this, "check")
                        .setAutoCancel(true)
                        .setContentTitle("收到订阅消息")
                        .setContentText("新闻消息")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .build();
                manager.notify(2, notificationcheck);
                openNotificationListenSettings();
                break;
        }
    }

    static class NosReceiver extends BroadcastReceiver {
        public void onReceive(Context context,Intent intent) {
            String[] nos =intent.getStringArrayExtra("nos");
            Toast.makeText(context, nos[0]+nos[1]+nos[2], Toast.LENGTH_SHORT).show();
        }
    }

}