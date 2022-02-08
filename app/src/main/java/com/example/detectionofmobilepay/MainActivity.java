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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Socket socket = null;
    private ServerSocket ss = null;
    private NosReceiver nosreceiver;
    private NotificationManager manager;
    private final int PORT = 5000;
    protected static final int MSG_ID = 0x13;
    public Boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        return packageNames.contains(context.getPackageName());
    }
    public Boolean BluetoothConnected(){
        return true;
    }
    public void SendtoBluetooth(String st){
        Toast.makeText(getApplicationContext(),st,Toast.LENGTH_SHORT).show();
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
        //设置开关
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch0 =(Switch)findViewById(R.id.switch1);
        switch0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    if(BluetoothConnected()){
                        Thread myCommsThread = new Thread(new CommsThread());
                        myCommsThread.start();
                    } else {
                        new Thread(() -> {
                            try {
                                InetAddress IPAdd = InetAddress.getByName("");
                                socket = new Socket(IPAdd,PORT);
                            } catch (UnknownHostException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1){
                                e1.printStackTrace();
                            }
                        });
                    }
                }
                else
                {
                    if(ss!=null)
                        try {
                            ss.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    if(socket!=null) try {
                        socket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        //设置按钮界面
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
        //连接蓝牙

    }
    protected void onDestroy() {
        super.onDestroy();
        manager.cancelAll();
        unregisterReceiver(nosreceiver);
        if(ss!=null)
            try {
                ss.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        if(socket!=null) try {
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
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
    private final Handler myhandler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg){
            if (msg.what==MSG_ID) {
                SendtoBluetooth(msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };
    class CommsThread implements Runnable {
         public void run() {
             Socket s = null;
             try {
                 ss = new ServerSocket(PORT);
                 while (!Thread.currentThread().isInterrupted()) {
                     Message m = new Message();
                     m.what = MSG_ID;
                     try {
                         if (s == null) s = ss.accept();
                         BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream(),StandardCharsets.UTF_8));
                         m.obj = input.readLine();
                         myhandler.sendMessage(m);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }

         }
    }

    class NosReceiver extends BroadcastReceiver {
        public void onReceive(Context context,Intent intent) {
            String[] nos =intent.getStringArrayExtra("nos");
            if(nos[0].equals("com.tencent.mm"))
                nos[0]="微信支付";
            else nos[0]="支付宝收款";
            Toast.makeText(context,nos[0]+nos[1], Toast.LENGTH_SHORT).show();
            if(BluetoothConnected()){
                SendtoBluetooth(nos[0]);
                SendtoBluetooth(nos[1]);
            }
            else{
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),StandardCharsets.UTF_8)),true);
                    out.println(nos[0]);
                    out.println(nos[1]);
                } catch (UnknownHostException e1){
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}