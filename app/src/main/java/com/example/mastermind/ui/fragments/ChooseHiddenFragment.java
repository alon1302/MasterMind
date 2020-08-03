package com.example.mastermind.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.mastermind.R;
import com.example.mastermind.model.game.GamePeg;
import com.example.mastermind.model.game.GameRow;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class ChooseHiddenFragment extends Fragment {


    private CircleImageView[] hiddenRowImages;
    GameRow hidden;
    private CircleImageView red, green, blue, orange, yellow, light;
    private CircleImageView current;
    String currentSelection = "null";
    View view;

    public ChooseHiddenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_choose_hidden, container, false);
        createButtons();
        createRow();
        return view;
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
            switch (stringGameRow[i]){
                case "null":
                    hiddenRowImages[i].setImageResource(R.color.colorTWhite);
                    break;
                case "red":
                    hiddenRowImages[i].setImageResource(R.color.colorRed);
                    break;
                case "green":
                    hiddenRowImages[i].setImageResource(R.color.colorGreen);
                    break;
                case "blue":
                    hiddenRowImages[i].setImageResource(R.color.colorBlue);
                    break;
                case "orange":
                    hiddenRowImages[i].setImageResource(R.color.colorOrange);
                    break;
                case "yellow":
                    hiddenRowImages[i].setImageResource(R.color.colorYellow);
                    break;
                case "light":
                    hiddenRowImages[i].setImageResource(R.color.colorLight);
                    break;
            }
        }
    }

    public void onPositionClicked(int position) {
        String lastSelection = hidden.getColorByPosition(position);
        hidden.addPeg(new GamePeg(currentSelection, position));
        Log.d(TAG, "onPositionClicked: " + hidden.toString());
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
        green = view.findViewById(R.id.choose_green);
        green.setOnTouchListener(onClickColorListener);
        blue = view.findViewById(R.id.choose_blue);
        blue.setOnTouchListener(onClickColorListener);
        orange = view.findViewById(R.id.choose_orange);
        orange.setOnTouchListener(onClickColorListener);
        yellow = view.findViewById(R.id.choose_yellow);
        yellow.setOnTouchListener(onClickColorListener);
        light = view.findViewById(R.id.choose_light);
        light.setOnTouchListener(onClickColorListener);
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
}