package com.example.mastermind.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.serviceAndBroadcast.BackMusicService;
import com.example.mastermind.model.serviceAndBroadcast.ComeBackBroadcast;
import com.example.mastermind.model.serviceAndBroadcast.NetworkChangeReceiver;
import com.example.mastermind.model.theme.Themes;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements MethodCallBack {

    public static final int ALARM_REQUEST = 0;

    private FirebaseAuth mAuth;

    private NetworkChangeReceiver networkReceiver;
    private Dialog offlineDialog;
    private Button btnOfflineMode;

    private TextView tv_coins;

    private ImageView iv_musicOnOff;
    private boolean isPlaying;
    private Intent service;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        User user = CurrentUser.getInstance();
        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(user.getName());

        CircleImageView iv_userImage = findViewById(R.id.iv_image);
        Glide.with(this).load(user.getImgUrl()).into(iv_userImage);

        networkReceiver = new NetworkChangeReceiver();
        registerReceiver(networkReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        createNotificationChannel();
        createOfflineDialog();
        showCoins();
    }

    @Override
    protected void onStart() {
        super.onStart();
        service = new Intent(getApplicationContext(), BackMusicService.class);
        iv_musicOnOff = findViewById(R.id.btn_Music);
        if (BackMusicService.isPlaying){
            isPlaying = true;
            iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_off_24);
        } else {
            isPlaying = false;
            iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_note_24);
        }
        iv_musicOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying){
                    startService(service);
                    iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_off_24);
                    isPlaying = true;
                } else {
                    stopService(service);
                    iv_musicOnOff.setImageResource(R.drawable.ic_baseline_music_note_24);
                    isPlaying = false;
                }
            }
        });
    }



    public void showCoins() {
        tv_coins = findViewById(R.id.textView_coins);
        try {
            FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(CurrentUser.getInstance().getId()).child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int coins = snapshot.getValue(Integer.class);
                        CurrentUser.setUserCoins(coins);
                        tv_coins.setText("" + coins);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(CurrentUser.getInstance().getId()).child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).setValue(0);
                        CurrentUser.setUserCoins(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(CurrentUser.getInstance().getId()).child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).setValue(0);
        }
    }

    public void onClickOnePlayer(View view) {
        Intent intent = new Intent(this, OnePlayerActivity.class);
        intent.putExtra(Const.INTENT_EXTRA_KEY_IS_ONLINE, true);
        startActivity(intent);
    }

    public void onClickTwoPlayer(View view) {
        Intent intent = new Intent(this, MultiplayerActivity.class);
        intent.putExtra(Const.INTENT_EXTRA_KEY_TYPE, Const.INTENT_EXTRA_VALUE_WITH_CODE);
        startActivity(intent);
    }

    public void onClickFindEnemy(View view) {
        Intent intent = new Intent(this, MultiplayerActivity.class);
        intent.putExtra(Const.INTENT_EXTRA_KEY_TYPE, Const.INTENT_EXTRA_VALUE_FIND_ENEMY);
        startActivity(intent);
    }


    public void onClickLogOut(View view) {
        Themes.getInstance(getApplicationContext()).logout();
        Intent intent = new Intent(this, LoginActivity.class);
        mAuth.signOut();
        CurrentUser.logout();
        startActivity(intent);
        finish();
    }

    public void onClickHowToPlay(View view) {
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }

    public void onClickThemes(View v) {
        Intent intent = new Intent(this, ThemesActivity.class);
        startActivity(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = Const.NOTIFICATION_CHANNEL_NAME;
            String description = "Click Here To Get Extra 300 Coins";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Const.NOTIFICATION_CHANNEL_NAME, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCallBack(int action, Object value) {
        toggleIsOnline(action);
    }

    private void toggleIsOnline(int mode) {
        if (mode == Const.ONLINE)
            offlineDialog.dismiss();
        else if (mode == Const.OFFLINE) {
            offlineDialog.show();
            btnOfflineMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, OnePlayerActivity.class);
                    intent.putExtra(Const.INTENT_EXTRA_KEY_IS_ONLINE, false);
                    startActivity(intent);
                }
            });
        }
    }

    public void createOfflineDialog(){
        offlineDialog = new Dialog(this);
        offlineDialog.setContentView(R.layout.dialog_offline);
        offlineDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btnOfflineMode = offlineDialog.findViewById(R.id.offlineModeBtn);
        offlineDialog.setCancelable(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getIntent().removeExtra(Const.INTENT_EXTRA_KEY_FROM);
        Intent intent = new Intent(HomeActivity.this, ComeBackBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, ALARM_REQUEST, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + Const.NOTIFICATION_TIME, pendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!BackMusicService.isPlaying) {
            stopService(service);
        }
        unregisterReceiver(networkReceiver);
    }
}
