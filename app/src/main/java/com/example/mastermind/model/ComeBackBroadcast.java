package com.example.mastermind.model;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mastermind.R;
import com.example.mastermind.ui.activities.HomeActivity;

public class ComeBackBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.putExtra(Const.INTENT_EXTRA_KEY_FROM, 1);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
