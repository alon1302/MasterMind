package com.example.mastermind.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mastermind.R;
import com.example.mastermind.model.Themes;
import com.example.mastermind.model.game.CheckRow;
import com.example.mastermind.model.game.GamePeg;
import com.example.mastermind.model.game.GameRow;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.user.CurrentUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class ChooseHiddenFragment extends Fragment {

    private static Random rnd;

    private CircleImageView[] hiddenRowImages;
    GameRow hidden;
    Context context;
    private CircleImageView red, green, blue, orange, yellow, light;
    private CircleImageView current;
    String currentSelection = "null";
    View view;
    String[] colors;

    private TextView countDownText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 30000;

    private Drawable theme;
    private HashMap<String, Integer> colorsMap;

    public ChooseHiddenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rnd = new Random();
        this.colors = new String[]{"red", "green", "blue", "orange", "yellow", "light"};
        context = requireActivity();

        SharedPreferences sharedPreferences = requireActivity().getApplicationContext().getSharedPreferences("ThemesPrefs:" + CurrentUser.getInstance().getId(), Context.MODE_PRIVATE);
        int useIndex = sharedPreferences.getInt("index", 0);
        int themeImg = Themes.getInstance(requireActivity().getApplicationContext()).getAllThemes().get(useIndex).getPegImage();
        theme = this.getResources().getDrawable(themeImg);
        createColorsMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  choose");
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
        colorsMap.put("null", R.color.colorTWhite);
        colorsMap.put("red", R.color.colorRed);
        colorsMap.put("green", R.color.colorGreen);
        colorsMap.put("blue", R.color.colorBlue);
        colorsMap.put("orange", R.color.colorOrange);
        colorsMap.put("yellow", R.color.colorYellow);
        colorsMap.put("light", R.color.colorLight);
    }

    public void randomizeHidden() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
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
                methodCallBack.onCallBack(4, row);
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
        else{
            Toast.makeText(requireActivity(), "choose your hidden row", Toast.LENGTH_SHORT).show();
        }
    }
    public void createRow() {
        hidden = new GameRow();
        hiddenRowImages = new CircleImageView[4];
        hiddenRowImages[0] = view.findViewById(R.id.choose_0);
        hiddenRowImages[1] = view.findViewById(R.id.choose_1);
        hiddenRowImages[2] = view.findViewById(R.id.choose_2);
        hiddenRowImages[3] = view.findViewById(R.id.choose_3);
        for (int i = 0; i < 4; i++) {
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
        for (int i =0; i<4;i++){
            if (!stringGameRow[i].equals("null"))
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
                currentSelection = "null";
                updateCurrImg();
            }
        });
    }

    public void updateCurrImg() {
        current.setImageResource(colorsMap.get(currentSelection));
        if (!currentSelection.equals("null"))
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