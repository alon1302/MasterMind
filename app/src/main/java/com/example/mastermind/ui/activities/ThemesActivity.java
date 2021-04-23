package com.example.mastermind.ui.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.theme.Themes;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.adapters.AdapterThemes;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThemesActivity extends AppCompatActivity implements MethodCallBack {

    TextView textViewCoins;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    AdapterThemes adapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        textViewCoins = findViewById(R.id.textView_coinsThemes);
        textViewCoins.setText("" + CurrentUser.getUserCoins());

        recyclerView = findViewById(R.id.recyclerView_themes);
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter = new AdapterThemes(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.startLayoutAnimation();

        sharedPreferences = getApplicationContext().getSharedPreferences(Const.SHARED_PREFERENCES_ID + CurrentUser.getInstance().getId(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCallBack(final int action, final Object value) {
        final int index = action;
        Drawable theme = (Drawable) value;
        if (!Themes.getInstance(ThemesActivity.this.getApplicationContext()).getAllThemes().get(index).isOpened()) {
            final Dialog d = new Dialog(this);
            d.setContentView(R.layout.dialog_get_theme);
            Objects.requireNonNull(d.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            final TextView coins = d.findViewById(R.id.textView_coinsGetTheme);
            coins.setText("" + CurrentUser.getUserCoins());
            CircleImageView[] images = new CircleImageView[6];
            images[0] = d.findViewById(R.id.red);
            images[1] = d.findViewById(R.id.green);
            images[2] = d.findViewById(R.id.blue);
            images[3] = d.findViewById(R.id.orange);
            images[4] = d.findViewById(R.id.yellow);
            images[5] = d.findViewById(R.id.light);
            for (int i = 0; i < images.length; i++)
                images[i].setForeground(theme);

            d.setCancelable(true);
            d.show();

            d.findViewById(R.id.buyThemeButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CurrentUser.getUserCoins() >= Const.THEME_COST) {
                        CurrentUser.addCoins(-1 * Const.THEME_COST);
                        Themes.getInstance(ThemesActivity.this.getApplicationContext()).getAllThemes().get(index).setOpened(true);
                        d.dismiss();
                        textViewCoins.setText("" + CurrentUser.getUserCoins());
                        Themes.getInstance(ThemesActivity.this).openATheme(index);
                        adapter.notifyDataSetChanged();
                        recyclerView.startLayoutAnimation();
                    } else
                        Toast.makeText(ThemesActivity.this, "You Have Not Enough Coins", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Themes.getInstance(ThemesActivity.this.getApplicationContext()).setCurrentThemeIndex(index);
        }
        adapter.notifyDataSetChanged();
        //}
    }
}