package com.example.mastermind.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.mastermind.R;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.adapters.AdapterThemes;

public class ThemesActivity extends AppCompatActivity {

    TextView coins;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdapterThemes adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        coins = findViewById(R.id.textView_coinsThemes);
        coins.setText("" + CurrentUser.getUserCoins());

        recyclerView = findViewById(R.id.recyclerView_themes);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter = new AdapterThemes(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}