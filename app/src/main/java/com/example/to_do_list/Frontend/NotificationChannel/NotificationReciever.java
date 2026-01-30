package com.example.to_do_list.Frontend.NotificationChannel;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.to_do_list.MainActivity;
import com.example.to_do_list.R;

public class NotificationReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("task_id", 0);
        String taskName = intent.getStringExtra("task_name");
        // Ensure notification channel is created
        Notify.createNotificationChannel(context);
        // Intent to open MainActivity or any other activity
        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openAppIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Notify.CHANNEL_ID)
                .setSmallIcon(R.drawable.to_do_list) // Replace with your notification icon
                .setContentTitle("Task Reminder")
                .setContentText("Don't forget to complete: " + taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Set the intent that will fire when the user taps the notification
        // Notify manager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(taskId, builder.build());
    }
}
