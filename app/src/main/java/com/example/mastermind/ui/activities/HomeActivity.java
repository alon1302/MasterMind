package com.example.mastermind.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.ComeBackBroadcast;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;

    FirebaseDatabase database;
    DatabaseReference myRef;
    User user;
    TextView tv_name;
    TextView tv_coins;
    CircleImageView circleImageView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        user = CurrentUser.getInstance();

        Log.d("", "onCreate: " + user.toString());

        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(user.getName());
        showCoins();

        circleImageView = findViewById(R.id.iv_image);
        Glide.with(this).load(user.getImgUrl()).into(circleImageView);

        createNotificationChannel();
    }

    public void showCoins(){
        tv_coins = findViewById(R.id.textView_coins);
        try {
            FirebaseDatabase.getInstance().getReference().child("Users/" + CurrentUser.getInstance().getId() + "/Collection/Coins").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int coins = snapshot.getValue(Integer.class);
                        tv_coins.setText("" + coins);
                        CurrentUser.setUserCoins(coins);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Users/" + CurrentUser.getInstance().getId() + "/Collection/Coins").setValue(0);
                        CurrentUser.setUserCoins(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            FirebaseDatabase.getInstance().getReference().child("Users/" + CurrentUser.getInstance().getId() + "/Collection/Coins").setValue(0);
        }
    }

    public void onClickOnePlayer(View view) {
        Intent intent = new Intent(this, OnePlayerActivity.class);
        startActivity(intent);
    }

    public void onClickTwoPlayer(View view){
        Intent intent = new Intent(this, MultiplayerActivity.class);
        intent.putExtra("type", "withCode");
        startActivity(intent);
    }
    public void onClickFindEnemy(View view){
        Intent intent = new Intent(this, MultiplayerActivity.class);
        intent.putExtra("type", "findEnemy");
        startActivity(intent);
    }


    public void onClickLogOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        mAuth.signOut();
        CurrentUser.logout();
        currentUser = null;
        startActivity(intent);
    }

    public void onClickHowToPlay(View view) {
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = "ComeBackReminder";
            String description = "we missed you' please come back";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Comeback", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(HomeActivity.this, ComeBackBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();
        long tenSecondsTest = 10000;
        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + tenSecondsTest, pendingIntent);
    }
}
