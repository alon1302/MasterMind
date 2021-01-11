package com.example.mastermind.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mastermind.R;
import com.example.mastermind.model.Theme;
import com.example.mastermind.model.Themes;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.adapters.AdapterThemes;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThemesActivity extends AppCompatActivity implements MethodCallBack
{

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

    @Override
    public void onCallBack(int action, Object value) {
        Dialog d = new Dialog(this);
        d.setContentView(R.layout.dialog_get_theme);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView coins = d.findViewById(R.id.textView_coinsGetTheme);
        coins.setText("" + CurrentUser.getUserCoins());
        CircleImageView[] images = new CircleImageView[6];
        images[0] = d.findViewById(R.id.red);
        images[1] = d.findViewById(R.id.green);
        images[2] = d.findViewById(R.id.blue);
        images[3] = d.findViewById(R.id.orange);
        images[4] = d.findViewById(R.id.yellow);
        images[5] = d.findViewById(R.id.light);
        for (int i = 0; i < images.length; i++) {
            images[i].setForeground((Drawable) value);
        }
        d.setCancelable(true);
        d.show();
    }
}