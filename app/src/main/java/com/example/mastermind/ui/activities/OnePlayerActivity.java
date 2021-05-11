package com.example.mastermind.ui.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.game.CheckRow;
import com.example.mastermind.model.game.GameManager;
import com.example.mastermind.model.game.GameRow;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.model.serviceAndBroadcast.BackMusicService;
import com.example.mastermind.model.theme.Themes;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.adapters.AdapterRows;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnePlayerActivity extends AppCompatActivity implements OnPegClickListener {

    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    private long timeInMillis;
    private long minutes, seconds;

    private boolean tookHint;

    private GameManager gameManager;
    private CircleImageView[] hiddenRowImages;
    private AdapterRows adapterRows;
    private RecyclerView recyclerView;
    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;

    private String currentSelection = Const.NULL_COLOR_IN_GAME;

    private CircleImageView red, green, blue, orange, yellow, light;
    private CircleImageView current;

    private TextView tv_coins;
    private Drawable theme;

    private ImageView iv_musicOnOff;
    private boolean isPlaying;
    private Intent service;

    private boolean isOnline;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);
        isOnline = getIntent().getBooleanExtra(Const.INTENT_EXTRA_KEY_IS_ONLINE,false);
        chronometer = findViewById(R.id.chronometer);

        if (isOnline) {
            tv_coins = findViewById(R.id.textView_coinsGame);
            tv_coins.setText("" + CurrentUser.getUserCoins());
        } else
            findViewById(R.id.imageView_hint).setVisibility(View.INVISIBLE);

        tookHint = false;
        gameManager = new GameManager();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        gameRows = gameManager.getGameRows();
        checkRows = gameManager.getCheckRows();
        adapterRows = new AdapterRows(gameRows, checkRows, this,true);
        recyclerView.setAdapter(adapterRows);
        current = findViewById(R.id.currentSelection);

        int useIndex = Themes.getInstance(getApplicationContext()).getCurrentThemeIndex();
        int themeImg = Themes.getInstance(getApplicationContext()).getAllThemes().get(useIndex).getPegImage();
        theme = this.getResources().getDrawable(themeImg);

        startTimeRunning();
        createHidden();
        createButtons();
    }

    @Override
    public void onStart() {
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

    public void createHidden() {
        hiddenRowImages = new CircleImageView[Const.ROW_SIZE];
        hiddenRowImages[0] = findViewById(R.id.hidden0);
        hiddenRowImages[1] = findViewById(R.id.hidden1);
        hiddenRowImages[2] = findViewById(R.id.hidden2);
        hiddenRowImages[3] = findViewById(R.id.hidden3);
        GameRow hiddenRow = gameManager.getHidden();
        String[] hiddenColors = hiddenRow.getStringRow();
        for (int i = 0; i < hiddenColors.length; i++) {
            hiddenRowImages[i].setVisibility(View.INVISIBLE);
            hiddenRowImages[i].setImageResource((Integer) Const.STRING_TO_COLOR_MAP.get(hiddenColors[i]));
            hiddenRowImages[i].setForeground(theme);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createButtons() {
        red = findViewById(R.id.red);
        red.setOnTouchListener(onClickColorListener);
        red.setForeground(theme);
        green = findViewById(R.id.green);
        green.setOnTouchListener(onClickColorListener);
        green.setForeground(theme);
        blue = findViewById(R.id.blue);
        blue.setOnTouchListener(onClickColorListener);
        blue.setForeground(theme);
        orange = findViewById(R.id.orange);
        orange.setOnTouchListener(onClickColorListener);
        orange.setForeground(theme);
        yellow = findViewById(R.id.yellow);
        yellow.setOnTouchListener(onClickColorListener);
        yellow.setForeground(theme);
        light = findViewById(R.id.light);
        light.setOnTouchListener(onClickColorListener);
        light.setForeground(theme);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelection = Const.NULL_COLOR_IN_GAME;
                updateCurrImg();
            }
        });
    }

    View.OnTouchListener onClickColorListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == red)
                currentSelection = Const.RED_COLOR_IN_GAME;
            else if (v == green)
                currentSelection = Const.GREEN_COLOR_IN_GAME;
            else if (v == blue)
                currentSelection = Const.BLUE_COLOR_IN_GAME;
            else if (v == orange)
                currentSelection = Const.ORANGE_COLOR_IN_GAME;
            else if (v == yellow)
                currentSelection = Const.YELLOW_COLOR_IN_GAME;
            else if (v == light)
                currentSelection = Const.LIGHT_COLOR_IN_GAME;
            updateCurrImg();
            return true;
        }
    };

    public void updateCurrImg() {
        current.setImageResource((Integer) Const.STRING_TO_COLOR_MAP.get(currentSelection));
        if (!currentSelection.equals(Const.NULL_COLOR_IN_GAME))
            current.setForeground(theme);
        else
            current.setForeground(null);
    }

    @Override
    public void onPositionClicked(int position) {
        String lastSelection = gameRows.get(gameManager.getTurn() - 1).getColorByPosition(position);
        gameManager.pegToGameRow(currentSelection, position);
        currentSelection = lastSelection;
        updateCurrImg();
        adapterRows.notifyDataSetChanged();
    }

    public void onClickSubmit(View view) {
        if (gameRows.get(gameManager.getTurn() - 1).isFull()) {
            if (!gameManager.isGameContinue()) {
                for (int i = 0; i < hiddenRowImages.length; i++)
                    hiddenRowImages[i].setVisibility(View.VISIBLE);
                Toast.makeText(this, "You Win", Toast.LENGTH_LONG).show();
                pauseTimeRunning();
                checkTime();
                openWinnerActivity();
            }
            recyclerView.smoothScrollToPosition(adapterRows.getItemCount() - 1);
            adapterRows.notifyDataSetChanged();
        }
    }

    @SuppressLint("SetTextI18n")
    public void onClickHint(View view){
        if (tookHint)
            Toast.makeText(OnePlayerActivity.this, "You Already took hint", Toast.LENGTH_SHORT).show();
        else {
            final Dialog hintDialog = new Dialog(this);
            hintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            hintDialog.setContentView(R.layout.dialog_get_hint);
            hintDialog.setCancelable(true);
            hintDialog.show();
            TextView coins = hintDialog.findViewById(R.id.textView_coinsHint);
            coins.setText("" + CurrentUser.getUserCoins());
            Button buy = hintDialog.findViewById(R.id.buyHintButton);
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CurrentUser.getUserCoins() < Const.HINT_COST)
                        Toast.makeText(OnePlayerActivity.this, "You Don't Have Enough Coins", Toast.LENGTH_SHORT).show();
                    else {
                        CurrentUser.addCoins(-1 * Const.HINT_COST);
                        int position = new Random().nextInt(Const.ROW_SIZE);
                        hiddenRowImages[position].setVisibility(View.VISIBLE);
                        tv_coins.setText("" + CurrentUser.getUserCoins());
                        tookHint = true;
                    }
                    hintDialog.dismiss();
                }
            });
        }
    }

    private void openWinnerActivity() {
        Intent intent = new Intent(this, WinActivity.class);
        intent.putExtra(Const.INTENT_EXTRA_KEY_IS_ONLINE, isOnline);
        intent.putExtra(Const.INTENT_EXTRA_KEY_MINUTES, minutes);
        intent.putExtra(Const.INTENT_EXTRA_KEY_SECONDS, seconds);
        intent.putExtra(Const.INTENT_EXTRA_KEY_TIME, timeInMillis);
        startActivity(intent);
        finish();
    }

    //_______________Timer_______________//
    public void startTimeRunning() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseTimeRunning() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void checkTime() {
        timeInMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        minutes = (timeInMillis / 1000) / 60;
        seconds = (timeInMillis / 1000) % 60;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!BackMusicService.isPlaying) {
            stopService(service);
        }
    }
}