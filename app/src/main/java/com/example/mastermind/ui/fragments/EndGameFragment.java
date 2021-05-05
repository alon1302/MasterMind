package com.example.mastermind.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.Const;
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

    private User user1,user2;
    private String code;
    private TextView indication;
    private int rematchResponse = 1;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_end_game, container, false);
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottie_winner);
        CircleImageView winnerIV = view.findViewById(R.id.Winner_image);
        CircleImageView loserIV = view.findViewById(R.id.Loser_image);
        indication = view.findViewById(R.id.indication);
        if (getArguments() != null) {
            user1 = (User) getArguments().getSerializable(Const.INTENT_EXTRA_KEY_USER1);
            user2 = (User) getArguments().getSerializable(Const.INTENT_EXTRA_KEY_USER2);
        } else
            requireActivity().finish();

        final String player = getArguments().getString(Const.INTENT_EXTRA_KEY_PLAYER);
        code = getArguments().getString(Const.INTENT_EXTRA_KEY_CODE);

        final int situation = getArguments().getInt(Const.INTENT_EXTRA_KEY_WHO_IS_WIN);
        String winner = "Something Went Wrong";
        if (situation == Const.END_GAME_SITUATION_TIE){
            winner = "It's a Tie";
            lottieAnimationView.setVisibility(View.INVISIBLE);
            Glide.with(this.requireActivity()).load(user1.getImgUrl()).into(winnerIV);
            Glide.with(this.requireActivity()).load(user2.getImgUrl()).into(loserIV);
            clearGame();
        }
        else if (situation == Const.END_GAME_SITUATION_WIN) {
            Glide.with(this.requireActivity()).load(user1.getImgUrl()).into(winnerIV);
            Glide.with(this.requireActivity()).load(user2.getImgUrl()).into(loserIV);
            winner = "You Win";
            clearGame();
        }
        else if (situation == Const.END_GAME_SITUATION_LOSE) {
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

        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.REMATCH_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    rematchResponse = 2;
                    indication.setVisibility(View.VISIBLE);
                    if (snapshot.getValue(Integer.class) >= 2) {
                        try {
                            Intent intent = new Intent(EndGameFragment.this.requireActivity(), MultiplayerActivity.class);
                            if (situation == Const.END_GAME_SITUATION_WIN) {
                                intent.putExtra(Const.INTENT_EXTRA_KEY_PLAYER1 , user1);
                                intent.putExtra(Const.INTENT_EXTRA_KEY_PLAYER2 , user2);
                            } else if (situation == Const.END_GAME_SITUATION_LOSE) {
                                intent.putExtra(Const.INTENT_EXTRA_KEY_PLAYER2 , user1);
                                intent.putExtra(Const.INTENT_EXTRA_KEY_PLAYER1 , user2);
                            } else {
                                if (player.equals(Const.PLAYER1)) {
                                    intent.putExtra(Const.INTENT_EXTRA_KEY_PLAYER2 , user2);
                                    intent.putExtra(Const.INTENT_EXTRA_KEY_PLAYER1 , user1);
                                } else {
                                    intent.putExtra(Const.INTENT_EXTRA_KEY_PLAYER1, user2);
                                    intent.putExtra(Const.INTENT_EXTRA_KEY_PLAYER2, user1);
                                }
                            }
                            intent.putExtra(Const.INTENT_EXTRA_KEY_CODE, code);
                            startActivity(intent);
                            EndGameFragment.this.requireActivity().finish();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.REMATCH_IN_FIREBASE).setValue(rematchResponse);
    }

    public void clearGame(){
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.GAME_IN_FIREBASE).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.WINNER_IN_FIREBASE).setValue(Const.NONE_IN_FIREBASE);
            }
        });
    }
}