package com.example.mastermind.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.firebase.MultiPlayerManager;
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.activities.MultiplayerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class EndGameFragment extends Fragment {

    User user1,user2;
    String code;
    LottieAnimationView lottieAnimationView;
    CircleImageView winnerIV,loserIV;
    TextView indication;
    int rematchResponse = 1;

    public EndGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_end_game, container, false);

        lottieAnimationView = view.findViewById(R.id.lottie_winner);
        winnerIV = view.findViewById(R.id.Winner_image);
        loserIV = view.findViewById(R.id.Loser_image);
        indication = view.findViewById(R.id.indication);
        user1 = (User) getArguments().getSerializable("user1");
        user2 = (User) getArguments().getSerializable("user2");
        final String player = getArguments().getString("player");
        code = getArguments().getString("code");

        final int situation = getArguments().getInt("whoIsWin");
        String winner = "DEBUG";
        if (situation == 0){
            winner = "Its a Tie";
            lottieAnimationView.setVisibility(View.INVISIBLE);
            Glide.with(this.requireActivity()).load(user1.getImgUrl()).into(winnerIV);
            Glide.with(this.requireActivity()).load(user2.getImgUrl()).into(loserIV);
            clearGame();
        }
        else if (situation == 1) {
            Glide.with(this.requireActivity()).load(user1.getImgUrl()).into(winnerIV);
            Glide.with(this.requireActivity()).load(user2.getImgUrl()).into(loserIV);
            winner = "You Win";
            clearGame();
        }
        else if (situation == 2) {
            Glide.with(this.requireActivity()).load(user2.getImgUrl()).into(winnerIV);
            Glide.with(this.requireActivity()).load(user1.getImgUrl()).into(loserIV);
            winner = "You Lose";
        }
        ((TextView)view.findViewById(R.id.winner)).setText(winner);

        view.findViewById(R.id.rematch).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                indication.setText("Waiting To Your Opponent");
                indication.setVisibility(View.VISIBLE);
                askForRematch();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Rematch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    rematchResponse = 2;
                    indication.setVisibility(View.VISIBLE);
                    if (snapshot.getValue(Integer.class) >= 2){
                        Intent intent = new Intent(EndGameFragment.this.requireActivity(), MultiplayerActivity.class);
                        if (situation == 1){
                            intent.putExtra("player1" , user1);
                            intent.putExtra("player2" , user2);
                        }else if (situation == 2){
                            intent.putExtra("player2" , user1);
                            intent.putExtra("player1" , user2);
                        }else {
                            if (player.equals("player1")){
                                intent.putExtra("player2" , user2);
                                intent.putExtra("player1" , user1);
                            }else {
                                intent.putExtra("player1", user2);
                                intent.putExtra("player2", user1);
                            }
                        }
                        intent.putExtra("code", code);
                        startActivity(intent);
                        EndGameFragment.this.requireActivity().finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public void askForRematch(){
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Rematch").setValue(rematchResponse);
    }

    public void clearGame(){
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Winner").setValue("None");
            }
        });
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
    }
}