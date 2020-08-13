package com.example.mastermind.ui.fragments;

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
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.adapters.AdapterRows;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpponentTurnFragment extends Fragment {

    User user1, user2;
    View view;

    RecyclerView recyclerView;
    AdapterRows adapterRows;

    ArrayList<GameRow> gameRows;
    ArrayList<CheckRow> checkRows;

    String code , player;

    public OpponentTurnFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        user1 = (User) bundle.get("user1");
        user2 = (User) bundle.get("user2");
        code = bundle.getString("code");
        player = bundle.getString("player");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_opponent_turn, container, false);
        Glide.with(requireActivity()).load(user2.getImgUrl()).into((CircleImageView) view.findViewById(R.id.opponent_multi_img));
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        gameRows = new ArrayList<>();
        checkRows = new ArrayList<>();
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
                    checkRows.add(new CheckRow());
                    adapterRows.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

    }

    public GameRow convertStringToGameRow(String row) {
        GameRow gameRow = new GameRow();
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
        return gameRow;
    }
}