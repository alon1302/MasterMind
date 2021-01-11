package com.example.mastermind.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mastermind.R;
import com.example.mastermind.model.game.*;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.adapters.AdapterRows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnePlayerActivity extends AppCompatActivity implements OnPegClickListener {

    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    private boolean tookHint;

    private AdapterRows adapterRows;
    private RecyclerView recyclerView;
    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;

    private CircleImageView[] hiddenRowImages;
    private GameManager gameManager;

    String currentSelection = "null";
    private HashMap<String, Integer> colors;

    private CircleImageView red, green, blue, orange, yellow, light;
    private CircleImageView current;

    private long timeInMillis;
    private long minutes, seconds;

    private TextView tv_coins;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player);

        createColorsMap();
        chronometer = findViewById(R.id.chronometer);
        tv_coins = findViewById(R.id.textView_coinsGame);
        tv_coins.setText("" + CurrentUser.getUserCoins());
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
        startTimeRunning();
        createHidden();
        createButtons();
    }

    public void createColorsMap(){
        colors = new HashMap<>();
        colors.put("null", R.color.colorTWhite);
        colors.put("red", R.color.colorRed);
        colors.put("green", R.color.colorGreen);
        colors.put("blue", R.color.colorBlue);
        colors.put("orange", R.color.colorOrange);
        colors.put("yellow", R.color.colorYellow);
        colors.put("light", R.color.colorLight);
    }

    @SuppressLint("SetTextI18n")
    public void onClickHint(View view){
        if (tookHint) {
            Toast.makeText(OnePlayerActivity.this, "You Already took hint", Toast.LENGTH_SHORT).show();
        } else {
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
                    if (CurrentUser.getUserCoins() < 500) {
                        Toast.makeText(OnePlayerActivity.this, "You Don't Have Enough Coins", Toast.LENGTH_SHORT).show();
                    } else {
                        CurrentUser.addCoins(-500);
                        int position = new Random().nextInt(4);
                        hiddenRowImages[position].setVisibility(View.VISIBLE);
                        tv_coins.setText("" + CurrentUser.getUserCoins());
                        tookHint = true;
                    }
                    hintDialog.dismiss();
                }
            });
        }
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
        gameManager.setMinutes(minutes);
        gameManager.setSeconds(seconds);
    }

    //_______________Listeners_______________//
    View.OnTouchListener onClickColorListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == red)
                currentSelection = "red";
            else if (v == green)
                currentSelection = "green";
            else if (v == blue)
                currentSelection = "blue";
            else if (v == orange)
                currentSelection = "orange";
            else if (v == yellow)
                currentSelection = "yellow";
            else if (v == light)
                currentSelection = "light";
            updateCurrImg();
            return true;
        }
    };

    @Override
    public void onPositionClicked(int position) {
        String lastSelection = gameRows.get(gameManager.getTurn() - 1).getColorByPosition(position);
        gameManager.pegToGameRow(currentSelection, position);
        currentSelection = lastSelection;
        updateCurrImg();
        adapterRows.notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createButtons() {
        red = findViewById(R.id.red);
        red.setOnTouchListener(onClickColorListener);
        green = findViewById(R.id.green);
        green.setOnTouchListener(onClickColorListener);
        blue = findViewById(R.id.blue);
        blue.setOnTouchListener(onClickColorListener);
        orange = findViewById(R.id.orange);
        orange.setOnTouchListener(onClickColorListener);
        yellow = findViewById(R.id.yellow);
        yellow.setOnTouchListener(onClickColorListener);
        light = findViewById(R.id.light);
        light.setOnTouchListener(onClickColorListener);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelection = "null";
                updateCurrImg();
            }
        });
    }

    public void onClickSubmit(View view) {
        if (gameRows.get(gameManager.getTurn() - 1).isFull()) {
            if (!gameManager.nextTurnIsNotWin()) {
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

    private void openWinnerActivity() {
        Intent intent = new Intent(this, WinActivity.class);
        intent.putExtra("minutes", minutes);
        intent.putExtra("seconds", seconds);
        intent.putExtra("time", timeInMillis);
        startActivity(intent);
        finish();
    }

    public void updateCurrImg() {
        current.setImageResource(colors.get(currentSelection));
    }

    public void createHidden() {
        hiddenRowImages = new CircleImageView[4];
        hiddenRowImages[0] = findViewById(R.id.hidden0);
        hiddenRowImages[1] = findViewById(R.id.hidden1);
        hiddenRowImages[2] = findViewById(R.id.hidden2);
        hiddenRowImages[3] = findViewById(R.id.hidden3);
        GameRow hiddenRow = gameManager.getHidden();
        String[] hiddenColors = hiddenRow.getStringRow();
        for (int i = 0; i < hiddenColors.length; i++) {
            hiddenRowImages[i].setVisibility(View.INVISIBLE);
            hiddenRowImages[i].setImageResource(colors.get(hiddenColors[i]));
        }
    }
}