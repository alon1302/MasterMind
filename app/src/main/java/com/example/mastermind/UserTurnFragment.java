package com.example.mastermind;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mastermind.model.game.CheckRow;
import com.example.mastermind.model.game.GameManager;
import com.example.mastermind.model.game.GameRow;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.ui.adapters.AdapterRows;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserTurnFragment extends Fragment implements OnPegClickListener {

    GameManager gameManager;

    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;

    private AdapterRows adapterRows;
    private RecyclerView recyclerView;
    private CircleImageView[] hiddenRowImages;

    private CircleImageView red, green, blue, orange, yellow, light;
    private CircleImageView current;

    String currentSelection = "null";
    View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_turn, container, false);
        recyclerView = view.findViewById(R.id.user_multi_recyclerView);
        gameManager = new GameManager();
        gameRows = gameManager.getGameRows();
        checkRows = gameManager.getCheckRows();
        adapterRows = new AdapterRows(gameRows, checkRows,requireActivity());
        recyclerView.setAdapter(adapterRows);
        createHidden();
        createButtons();
        return view;
    }

    public void onClickSubmit(View view) {
        if (gameRows.get(gameManager.getTurn() - 1).isFull()) {
            if (!gameManager.nextTurn()) {
                for (int i = 0; i < hiddenRowImages.length; i++) {
                    hiddenRowImages[i].setVisibility(View.VISIBLE);
                }
                //String s = convertGameRowToString();
                //FirebaseDatabase.getInstance().getReference().child("games").child("" + (gameManager.getTurn() - 1)).setValue(s);
               // Toast.makeText(this, "You Win", Toast.LENGTH_LONG).show();
                //pauseTimeRunning();
                //checkTime();
                //openWinnerActivity();
            }

            recyclerView.smoothScrollToPosition(adapterRows.getItemCount() - 1);
            adapterRows.notifyDataSetChanged();
           // String s = convertGameRowToString();
//            FirebaseDatabase.getInstance().getReference().child("games").child("" + (gameManager.getTurn() - 1)).setValue(s);
        }
    }

    public void createHidden() {
        hiddenRowImages = new CircleImageView[4];
        hiddenRowImages[0] = view.findViewById(R.id.user_multi_hidden0);
        hiddenRowImages[1] = view.findViewById(R.id.user_multi_hidden1);
        hiddenRowImages[2] = view.findViewById(R.id.user_multi_hidden2);
        hiddenRowImages[3] = view.findViewById(R.id.user_multi_hidden3);
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

    @SuppressLint("ClickableViewAccessibility")
    public void createButtons() {
        current = view.findViewById(R.id.user_multi_currentSelection);
        red = view.findViewById(R.id.user_multi_red);
        red.setOnTouchListener(onClickColorListener);
        green = view.findViewById(R.id.user_multi_green);
        green.setOnTouchListener(onClickColorListener);
        blue = view.findViewById(R.id.user_multi_blue);
        blue.setOnTouchListener(onClickColorListener);
        orange = view.findViewById(R.id.user_multi_orange);
        orange.setOnTouchListener(onClickColorListener);
        yellow = view.findViewById(R.id.user_multi_yellow);
        yellow.setOnTouchListener(onClickColorListener);
        light = view.findViewById(R.id.user_multi_light);
        light.setOnTouchListener(onClickColorListener);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelection = "null";
                updateCurrImg();
            }
        });
    }

    @Override
    public void onPositionClicked(int position) {
        String lastSelection = gameRows.get(gameManager.getTurn() - 1).getColorByPosition(position);
        gameManager.pegToGameRow(currentSelection, position);
        currentSelection = lastSelection;
        updateCurrImg();
        MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
        methodCallBack.onCallBack(7,gameRows.get(gameManager.getTurn()-1).getNumStringRow() + "|"+(gameManager.getTurn()-1));
        adapterRows.notifyDataSetChanged();
    }
}