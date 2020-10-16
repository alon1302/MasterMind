package com.example.mastermind.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mastermind.R;
import com.example.mastermind.model.game.*;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.adapters.AdapterRows;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnePlayerActivity extends AppCompatActivity implements OnPegClickListener {

    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;

    private AdapterRows adapterRows;
    private RecyclerView recyclerView;
    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;

    private CircleImageView[] hiddenRowImages;
    private GameManager gameManager;

    String currentSelection = "null";

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

        chronometer = findViewById(R.id.chronometer);
        tv_coins = findViewById(R.id.textView_coinsGame);
        tv_coins.setText("" + CurrentUser.getUserCoins());
        gameManager = new GameManager();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        gameRows = gameManager.getGameRows();
        checkRows = gameManager.getCheckRows();
        adapterRows = new AdapterRows(gameRows, checkRows, this);
        recyclerView.setAdapter(adapterRows);
        current = findViewById(R.id.currentSelection);
        startTimeRunning();
        createHidden();
        createButtons();
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
            if (v == red) {
                currentSelection = "red";
            } else if (v == green) {
                currentSelection = "green";
            } else if (v == blue) {
                currentSelection = "blue";
            } else if (v == orange) {
                currentSelection = "orange";
            } else if (v == yellow) {
                currentSelection = "yellow";
            } else if (v == light) {
                currentSelection = "light";
            }
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
                for (int i = 0; i < hiddenRowImages.length; i++) {
                    hiddenRowImages[i].setVisibility(View.VISIBLE);
                }
                //String s = convertGameRowToString();
                //FirebaseDatabase.getInstance().getReference().child("games").child("" + (gameManager.getTurn() - 1)).setValue(s);
                Toast.makeText(this, "You Win", Toast.LENGTH_LONG).show();
                pauseTimeRunning();
                checkTime();
                openWinnerActivity();
            }
            recyclerView.smoothScrollToPosition(adapterRows.getItemCount() - 1);
            adapterRows.notifyDataSetChanged();
//            String s = convertGameRowToString();
//            FirebaseDatabase.getInstance().getReference().child("games").child("" + (gameManager.getTurn() - 1)).setValue(s);
        }
    }

    private String convertGameRowToString() {
        GameRow currG = gameRows.get(gameManager.getTurn() - 2);
        String[] currGstrings = currG.getStringRow();
        CheckRow currC = checkRows.get(gameManager.getTurn() - 2);
        String[] currCstrings = currC.getStringRow();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            switch (currGstrings[i]) {
                case "red":
                    s.append("0");
                    break;
                case "green":
                    s.append("1");
                    break;
                case "blue":
                    s.append("2");
                    break;
                case "orange":
                    s.append("3");
                    break;
                case "yellow":
                    s.append("4");
                    break;
                case "light":
                    s.append("5");
                    break;
            }
        }
        for (int i = 0; i < 4; i++) {
            switch (currCstrings[i]) {
                case "black":
                    s.append("&");
                    break;
                case "white":
                    s.append("|");
                    break;
            }
        }
        return s.toString();
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
        switch (currentSelection) {
            case "null":
                current.setImageResource(R.color.colorTWhite);
                break;
            case "red":
                current.setImageResource(R.color.colorRed);
                break;
            case "blue":
                current.setImageResource(R.color.colorBlue);
                break;
            case "green":
                current.setImageResource(R.color.colorGreen);
                break;
            case "orange":
                current.setImageResource(R.color.colorOrange);
                break;
            case "yellow":
                current.setImageResource(R.color.colorYellow);
                break;
            case "light":
                current.setImageResource(R.color.colorLight);
                break;
        }
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
            //TODO // hiddenRowImages[i].setVisibility(View.INVISIBLE);
            switch (hiddenColors[i]) {
                case "null":
                    this.hiddenRowImages[i].setImageResource(R.color.colorTWhite);
                    break;
                case "red":
                    this.hiddenRowImages[i].setImageResource(R.color.colorRed);
                    break;
                case "green":
                    this.hiddenRowImages[i].setImageResource(R.color.colorGreen);
                    break;
                case "blue":
                    this.hiddenRowImages[i].setImageResource(R.color.colorBlue);
                    break;
                case "orange":
                    this.hiddenRowImages[i].setImageResource(R.color.colorOrange);
                    break;
                case "yellow":
                    this.hiddenRowImages[i].setImageResource(R.color.colorYellow);
                    break;
                case "light":
                    this.hiddenRowImages[i].setImageResource(R.color.colorLight);
                    break;
            }
        }
    }
}