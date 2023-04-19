package com.gitlab.schoolsystem;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.activity.result.ActivityResult;
import androidx.lifecycle.ViewModelProvider;

public class App extends Application {
    public static final String CHANNEL_ID = "SchoolSystemChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);
            NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_ID,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("School System's alarm channel");
                manager.createNotificationChannel(channel);
            }
        }
    }
}
