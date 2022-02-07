package com.example.detectionofmobilepay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private NosReceiver nosreceiver;
    private NotificationManager manager;
    public Boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        return packageNames.contains(context.getPackageName());
    }
    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
    //PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    protected void onStart(){
        super.onStart();
        TextView t1 = findViewById(R.id.StateofService);
        t1.setText(isNotificationListenerEnabled(this)?"True":"False");//通知权限状态指示器
        ///设置按钮界面
        Button check = findViewById(R.id.check);
        check.setOnClickListener(this);
        Button send = findViewById(R.id.send);
        send.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
        filter.addAction("android.service.notification.NotificationListenerService");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);//获取系统通知服务
        nosreceiver = new NosReceiver();
        registerReceiver(nosreceiver, filter);// 注册Broadcast Receiver
        if(!isNotificationListenerEnabled(this))
        {
            //设置状态指示器
            Notification notification = new NotificationCompat.Builder(this,"check")
                    .setContentTitle("未开启通知权限")
                    .setContentText("请在设置中打开")
                    //.setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);
        }
        ///编写通知渠道
        String channelId = "state";
        String channelName = "应用状态";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        createNotificationChannel(channelId, channelName, importance);
        channelId = "check";
        channelName = "权限状态";
        importance = NotificationManager.IMPORTANCE_HIGH;
        createNotificationChannel(channelId, channelName, importance);
        //设置状态指示器
        Notification notification2 = new NotificationCompat.Builder(this,"check")
                .setContentTitle("DMP正在运行")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setWhen(System.currentTimeMillis())
                .build();
        notification2.flags |= Notification.FLAG_NO_CLEAR;
        manager.notify(2,notification2);
    }
    protected void onDestroy(){
        super.onDestroy();
        manager.cancelAll();
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
                Notification notification3 = new NotificationCompat.Builder(this,"check")
                        .setContentTitle("test")
                        .setContentText("this is a test")
                        //.setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .build();
                manager.notify(3, notification3);
                break;
            case R.id.check:   //打开设置界面以获取服务权限
                startActivity(intent);
                break;
        }
    }

    static class NosReceiver extends BroadcastReceiver {
        public void onReceive(Context context,Intent intent) {
            String[] nos =intent.getStringArrayExtra("nos");
            if(nos[0].equals("com.tencent.mm"))
                nos[0]="微信支付";
            else nos[0]="支付宝收款";
            Toast.makeText(context,nos[0]+nos[1], Toast.LENGTH_SHORT).show();
        }
    }
}