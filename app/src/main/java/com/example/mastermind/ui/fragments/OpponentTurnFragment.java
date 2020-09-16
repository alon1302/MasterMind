package com.example.mastermind.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.game.CheckRow;
import com.example.mastermind.model.game.GameManager;
import com.example.mastermind.model.game.GamePeg;
import com.example.mastermind.model.game.GameRow;
import com.example.mastermind.model.listeners.SendHiddenToOpponent;
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.adapters.AdapterRows;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpponentTurnFragment extends Fragment implements SendHiddenToOpponent {

    User user1, user2;
    View view;

    RecyclerView recyclerView;
    AdapterRows adapterRows;

    GameManager gameManager;
    ArrayList<GameRow> gameRows;
    ArrayList<CheckRow> checkRows;
    String hidden;

    CircleImageView[] hiddenRowImages;

    String code , player;
    private Context context;

    ValueEventListener valueEventListener;
    private GameRow hiddenRow;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        user1 = (User) bundle.get("user1");
        user2 = (User) bundle.get("user2");
        code = bundle.getString("code");
        player = bundle.getString("player");
        gameManager = new GameManager();
        hiddenRow = new GameRow();
        for (int i = 0; i < 4; i++) {
            switch (hidden.charAt(i)) {
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
        gameManager = new GameManager();
        gameRows = gameManager.getGameRows();
        checkRows = gameManager.getCheckRows();
        hiddenRowImages = new CircleImageView[4];
        context = requireActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_opponent_turn, container, false);
        Glide.with(requireActivity()).load(user2.getImgUrl()).into((CircleImageView) view.findViewById(R.id.opponent_multi_img));
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView = view.findViewById(R.id.opponent_multi_recyclerView);
        adapterRows = new AdapterRows(gameRows, checkRows, requireActivity());
        recyclerView.setAdapter(adapterRows);
        recyclerView.setLayoutManager(layoutManager);


        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").child("Turns").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    int turn = Integer.parseInt(ds.getKey());
                    String other = "Player1";
                    if (player.equals("Player1"))
                        other = "Player2";
                    String row = ds.child(other).getValue(String.class);
                    if (gameRows.size() == turn){
                        gameRows.add(turn, convertStringToGameRow(row));
                    }
                    else {
                        gameRows.set(turn, convertStringToGameRow(row));
                    }
                    checkRows.add(turn, gameRows.get(turn).checkGameRow(hiddenRow));
                    adapterRows.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String turn = snapshot.getValue(String.class);
//                if (turn.equals(player)) {
//                    MethodCallBack methodCallBack = (MethodCallBack)context;
//                    methodCallBack.onCallBack(5, null);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("HowsTurn").addValueEventListener(valueEventListener);
        createRow();
        return view;

    }

    public GameRow convertStringToGameRow(String row) {
        GameRow gameRow = new GameRow();
        if (row != null) {
            for (int i = 0; i < 4; i++) {
                switch (row.charAt(i)) {
                    case 'n':
                        gameRow.addPeg(new GamePeg("null", i));
                        break;
                    case '0':
                        gameRow.addPeg(new GamePeg("red", i));
                        break;
                    case '1':
                        gameRow.addPeg(new GamePeg("green", i));
                        break;
                    case '2':
                        gameRow.addPeg(new GamePeg("blue", i));
                        break;
                    case '3':
                        gameRow.addPeg(new GamePeg("orange", i));
                        break;
                    case '4':
                        gameRow.addPeg(new GamePeg("yellow", i));
                        break;
                    case '5':
                        gameRow.addPeg(new GamePeg("light", i));
                        break;
                }
            }
        }
        return gameRow;
    }

    private void createRow(){
        hiddenRowImages[0] = view.findViewById(R.id.opponent_multi_hidden0);
        hiddenRowImages[1] = view.findViewById(R.id.opponent_multi_hidden1);
        hiddenRowImages[2] = view.findViewById(R.id.opponent_multi_hidden2);
        hiddenRowImages[3] = view.findViewById(R.id.opponent_multi_hidden3);

        String[] hiddenColors = hiddenRow.getStringRow();
        for (int i = 0; i < hiddenRowImages.length; i++) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void sendHidden(String hidden) {
        this.hidden = hidden;
    }
}