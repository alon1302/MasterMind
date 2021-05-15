package com.example.mastermind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mastermind.R;
import com.example.mastermind.model.user.CurrentUser;

public class NotificationActivity extends AppCompatActivity {

    private static int DELAY = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_intent);

        CurrentUser.addCoinsNotification();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DELAY = 0;
                finish();
                startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
            }
        }, DELAY);
    }
}