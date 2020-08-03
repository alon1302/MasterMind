package com.example.mastermind.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mastermind.R;
import com.example.mastermind.ui.fragments.AboutFragment;
import com.example.mastermind.ui.fragments.ChooseHiddenFragment;

public class MultiplayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, new ChooseHiddenFragment()).commit();

    }
}