package com.example.myalarmapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private Integer alarmHour, alarmMinute;
        private Ringtone ringtone;
        private Timer t = new Timer();
        public static final String TAG = "alarm";
        public static final String CHANNEL_ID = "MyNotiChannel";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmHour = intent.getIntExtra("alarmHour", 0);
        alarmMinute = intent.getIntExtra("alarmMinute", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(alarmHour,alarmMinute);
        }

        return super.onStartCommand(intent, flags, startId);

    }

    private void startForegroundService(Integer alarmHour, Integer alarmMinute) {

        Log.d(TAG, "onStartCommand: " +"started"+"...."+alarmHour+"......"+alarmMinute);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        try {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("My Alarm")
                    .setContentText("Alarm Time -- "+ alarmHour.toString()+" : "+alarmMinute.toString())
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Alarm Clock", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(Calendar.getInstance().getTime().getHours() == alarmHour &&
                        Calendar.getInstance().getTime().getMinutes() == alarmMinute){
                    ringtone.play();
                    
                    Log.d(TAG, "run: " + "started");
                }else{
                    ringtone.stop();
                }
            }
        },0, 2000);
    }

    @Override
    public void onDestroy() {
        ringtone.stop();
        t.cancel();
        super.onDestroy();
    }
}
