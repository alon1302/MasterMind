package com.example.mastermind.model.serviceAndBroadcast;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.activities.NotificationActivity;

public class ComeBackBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CurrentUser.resetNotification();
        Intent resultIntent = new Intent(context, NotificationActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,1, resultIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_NAME)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Master Mind")
                .setContentText("Click Here To Get Extra 300 Coins")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
