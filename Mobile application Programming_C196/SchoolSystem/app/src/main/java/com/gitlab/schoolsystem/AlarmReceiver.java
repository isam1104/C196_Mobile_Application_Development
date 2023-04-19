package com.gitlab.schoolsystem;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = "BroadcastReceiver";
    public static final String CHANNEL_ID = "SchoolSystemChannel";
    @Override
    public void onReceive(Context context, Intent intent) {
        //create a unique notification ID
        long timestamp = System.currentTimeMillis();
        int notificationID = (int) timestamp;

        String message  = intent.getStringExtra("message");
        if(message == null) message = "An item is due on your school app";

        Log.d(TAG, "onReceive: " + message);

        Intent launchIntent = new Intent(context, TermActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setContentTitle("School Alert")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }
}
