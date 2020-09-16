package com.example.mastermind.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.game.CheckRow;
import com.example.mastermind.model.game.GameManager;
import com.example.mastermind.model.game.GamePeg;
import com.example.mastermind.model.game.GameRow;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.adapters.AdapterRows;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;


public class UserTurnFragment extends Fragment implements OnPegClickListener {

    GameManager gameManager = new GameManager();;

    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;

    private AdapterRows adapterRows;
    private RecyclerView recyclerView;
    private CircleImageView[] hiddenRowImages;

    private CircleImageView red, green, blue, orange, yellow, light;
    private CircleImageView current;

    ValueEventListener valueEventListener;

    GameRow hiddenRow;
    String currentSelection = "null";
    View view;
    User user1, user2;
    String player;
    Context context;
    private String code;

    boolean isWaitingForWin;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: +++++++++++++++++++++++++++++++++++++++++++++");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        user1 = (User) bundle.get("user1");
        user2 = (User) bundle.get("user2");
        player = bundle.getString("player");
        code = bundle.getString("code");
        hiddenRow = new GameRow();

        String opponent = bundle.getString("opponent");

        if (player.equals("Player1"))
            opponent = "Player2";
        else
            opponent = "Player1";
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").child(opponent + "Hidden").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    hiddenRow= new GameRow();
                    for (int i = 0; i < 4; i++) {
                        switch (snapshot.getValue(String.class).charAt(i)) {
                            case 'n':
                                hiddenRow.addPeg(new GamePeg("null", i));
                                break;
                            case '0':
                                hiddenRow.addPeg(new GamePeg("red", i));
                                break;
                            case '1':
                                hiddenRow.addPeg(new GamePeg("green", i));
                                break;
                            case '2':
                                hiddenRow.addPeg(new GamePeg("blue", i));
                                break;
                            case '3':
                                hiddenRow.addPeg(new GamePeg("orange", i));
                                break;
                            case '4':
                                hiddenRow.addPeg(new GamePeg("yellow", i));
                                break;
                            case '5':
                                hiddenRow.addPeg(new GamePeg("light", i));
                                break;
                        }
                    }
                    gameManager.setHidden(hiddenRow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String hidden = bundle.getString("opponentHidden");
        Log.d(TAG, "onCreate: " + hidden + "                    5");
        Log.d(TAG, "onCreate: " + hiddenRow.toString()+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("onCreateView + UserTurn", "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_user_turn, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.user_multi_recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        gameRows = gameManager.getGameRows();
        checkRows = gameManager.getCheckRows();
        adapterRows = new AdapterRows(gameRows, checkRows,requireActivity());
        recyclerView.setAdapter(adapterRows);
        createHidden();
        createButtons();
        Glide.with(requireActivity()).load(user1.getImgUrl()).into((CircleImageView)view.findViewById(R.id.user_multi_img));
        view.findViewById(R.id.user_multi_btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSubmit();
            }
        });


        Log.d(TAG, "onCreateView:_______________ " + gameManager.getCheckRows().toString() + "________________________________________");




        context = requireActivity();
        return view;
    }

    public void onClickSubmit() {
        if (gameRows.get(gameManager.getTurn() - 1).isFull()) {
            checkRows.add(gameManager.getTurn() - 1, gameRows.get(gameManager.getTurn() - 1).checkGameRow(hiddenRow));
            if (gameManager.isWin()){
                if (player.equals("Player2")){
                    Toast.makeText(context, "You Win!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "You Win! but wait for the last turn", Toast.LENGTH_SHORT).show();
                    MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
                    methodCallBack.onCallBack(10, null);
                }
            }
            Log.d(TAG, "onClickSubmit: " + gameManager.getCheckRows().toString());
            MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
            methodCallBack.onCallBack(9, null);
            recyclerView.smoothScrollToPosition(adapterRows.getItemCount() - 1);
            adapterRows.notifyDataSetChanged();

        }else {
            Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show();
        }
    }



    public void createHidden() {
        hiddenRowImages = new CircleImageView[4];
        hiddenRowImages[0] = view.findViewById(R.id.user_multi_hidden0);
        hiddenRowImages[1] = view.findViewById(R.id.user_multi_hidden1);
        hiddenRowImages[2] = view.findViewById(R.id.user_multi_hidden2);
        hiddenRowImages[3] = view.findViewById(R.id.user_multi_hidden3);
        String[] hiddenColors = hiddenRow.getStringRow();
        for (int i = 0; i < hiddenColors.length; i++) {
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
        Log.d(TAG, "onPositionClicked: " + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        String lastSelection = gameRows.get(gameManager.getTurn() - 1).getColorByPosition(position);
        gameManager.pegToGameRow(currentSelection, position);
        currentSelection = lastSelection;
        updateCurrImg();
        MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
        methodCallBack.onCallBack(7,gameRows.get(gameManager.getTurn()-1).getNumStringRow() + "|"+(gameManager.getTurn()-1));
        adapterRows.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}