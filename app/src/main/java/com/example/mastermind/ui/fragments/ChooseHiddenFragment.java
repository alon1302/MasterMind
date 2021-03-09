package com.example.mastermind.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.game.GamePeg;
import com.example.mastermind.model.game.GameRow;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.theme.Themes;
import com.example.mastermind.model.user.CurrentUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseHiddenFragment extends Fragment {

    private static Random rnd;

    private CircleImageView[] hiddenRowImages;
    GameRow hidden;
    Context context;
    private CircleImageView red, green, blue, orange, yellow, light;
    private CircleImageView current;
    String currentSelection = Const.NULL_COLOR_IN_GAME;
    View view;
    String[] colors;

    private TextView countDownText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 30000;

    private Drawable theme;
    private HashMap<String, Integer> colorsMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rnd = new Random();
        this.colors = new String[]{Const.RED_COLOR_IN_GAME, Const.GREEN_COLOR_IN_GAME, Const.BLUE_COLOR_IN_GAME, Const.ORANGE_COLOR_IN_GAME, Const.YELLOW_COLOR_IN_GAME, Const.LIGHT_COLOR_IN_GAME,};
        context = requireActivity();

        SharedPreferences sharedPreferences = requireActivity().getApplicationContext().getSharedPreferences( Const.SHARED_PREFERENCES_ID + CurrentUser.getInstance().getId(), Context.MODE_PRIVATE);
        int useIndex = sharedPreferences.getInt(Const.SHARED_PREFERENCES_KEY_INDEX, 0);
        int themeImg = Themes.getInstance(requireActivity().getApplicationContext()).getAllThemes().get(useIndex).getPegImage();
        theme = this.getResources().getDrawable(themeImg);
        createColorsMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_choose_hidden, container, false);
        createButtons();
        createRow();
        Button button = view.findViewById(R.id.btn_submit_choose);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubmit();
            }
        });
        countDownText = view.findViewById(R.id.countDownText);
        startTimer();
        return view;
    }

    public void createColorsMap(){
        colorsMap = new HashMap<>();
        colorsMap.put(Const.NULL_COLOR_IN_GAME, R.color.colorTWhite);
        colorsMap.put(Const.RED_COLOR_IN_GAME, R.color.colorRed);
        colorsMap.put(Const.GREEN_COLOR_IN_GAME, R.color.colorGreen);
        colorsMap.put(Const.BLUE_COLOR_IN_GAME, R.color.colorBlue);
        colorsMap.put(Const.ORANGE_COLOR_IN_GAME, R.color.colorOrange);
        colorsMap.put(Const.YELLOW_COLOR_IN_GAME, R.color.colorYellow);
        colorsMap.put(Const.LIGHT_COLOR_IN_GAME, R.color.colorLight);
    }

    public void randomizeHidden() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            int num = rnd.nextInt(6);
            while (arrayList.contains(num)) {
                num = rnd.nextInt(6);
            }
            hidden.addPeg(new GamePeg(this.colors[num], i));
            arrayList.add(num);
        }
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                randomizeHidden();
                String row = hidden.getNumStringRow();
                MethodCallBack methodCallBack = (MethodCallBack)context;
                methodCallBack.onCallBack(Const.ACTION_HIDDEN_TO_FIREBASE, row);
            }
        }.start();
    }

    private void updateCountDownText() {
        int timeLeftInSeconds = (int) timeLeftInMillis / 1000;
        String timer = "00:";
        if (timeLeftInSeconds >= 10)
            timer += timeLeftInSeconds;
        else
            timer += "0" + timeLeftInSeconds;
        countDownText.setText(timer);
    }

    private void onClickSubmit(){
        if (hidden.isFull()){
            countDownTimer.cancel();
            String row = hidden.getNumStringRow();
            MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
            methodCallBack.onCallBack(4, row);
        }
        else
            Toast.makeText(requireActivity(), "choose your hidden row", Toast.LENGTH_SHORT).show();
    }
    public void createRow() {
        hidden = new GameRow();
        hiddenRowImages = new CircleImageView[Const.ROW_SIZE];
        hiddenRowImages[0] = view.findViewById(R.id.choose_0);
        hiddenRowImages[1] = view.findViewById(R.id.choose_1);
        hiddenRowImages[2] = view.findViewById(R.id.choose_2);
        hiddenRowImages[3] = view.findViewById(R.id.choose_3);
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            final int finalI = i;
            hiddenRowImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPositionClicked(finalI);
                }
            });
        }
    }

    public void updateUI(){
        String[] stringGameRow = hidden.getStringRow();
        for (int i = 0; i < Const.ROW_SIZE;i++){
            if (!stringGameRow[i].equals(Const.NULL_COLOR_IN_GAME))
                hiddenRowImages[i].setForeground(theme);
            else
                hiddenRowImages[i].setForeground(null);
            hiddenRowImages[i].setImageResource(colorsMap.get(stringGameRow[i]));
        }
    }

    public void onPositionClicked(int position) {
        String lastSelection = hidden.getColorByPosition(position);
        hidden.addPeg(new GamePeg(currentSelection, position));
        currentSelection = lastSelection;
        updateCurrImg();
        updateUI();
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

    @SuppressLint("ClickableViewAccessibility")
    public void createButtons() {
        red = view.findViewById(R.id.choose_red);
        red.setOnTouchListener(onClickColorListener);
        red.setForeground(theme);
        green = view.findViewById(R.id.choose_green);
        green.setOnTouchListener(onClickColorListener);
        green.setForeground(theme);
        blue = view.findViewById(R.id.choose_blue);
        blue.setOnTouchListener(onClickColorListener);
        blue.setForeground(theme);
        orange = view.findViewById(R.id.choose_orange);
        orange.setOnTouchListener(onClickColorListener);
        orange.setForeground(theme);
        yellow = view.findViewById(R.id.choose_yellow);
        yellow.setOnTouchListener(onClickColorListener);
        yellow.setForeground(theme);
        light = view.findViewById(R.id.choose_light);
        light.setOnTouchListener(onClickColorListener);
        light.setForeground(theme);
        current = view.findViewById(R.id.currentSelection);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelection = Const.NULL_COLOR_IN_GAME;
                updateCurrImg();
            }
        });
    }

    public void updateCurrImg() {
        current.setImageResource(colorsMap.get(currentSelection));
        if (!currentSelection.equals(Const.NULL_COLOR_IN_GAME))
            current.setForeground(theme);
        else
            current.setForeground(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }
}