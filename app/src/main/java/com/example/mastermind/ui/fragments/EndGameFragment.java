package com.example.mastermind.ui.fragments;

import android.os.Bundle;

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

import de.hdodenhof.circleimageview.CircleImageView;


public class EndGameFragment extends Fragment {

    User user1,user2;
    LottieAnimationView lottieAnimationView;
    CircleImageView winnerIV,loserIV;

    public EndGameFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_end_game, container, false);

        lottieAnimationView = view.findViewById(R.id.lottie_winner);
        winnerIV = view.findViewById(R.id.Winner_image);
        loserIV = view.findViewById(R.id.Loser_image);

        user1 = (User) getArguments().getSerializable("user1");
        user2 = (User) getArguments().getSerializable("user2");

        int situation = getArguments().getInt("whoIsWin");
        String winner = "DEBUG";
        if (situation == 0){
            winner = "Its a Tie";
            lottieAnimationView.setVisibility(View.INVISIBLE);
            Glide.with(this.requireActivity()).load(user1.getImgUrl()).into(winnerIV);
            Glide.with(this.requireActivity()).load(user2.getImgUrl()).into(loserIV);
        }
        else if (situation == 1) {
            Glide.with(this.requireActivity()).load(user1.getImgUrl()).into(winnerIV);
            Glide.with(this.requireActivity()).load(user2.getImgUrl()).into(loserIV);
            winner = "You Win";
        }
        else if (situation == 2) {
            Glide.with(this.requireActivity()).load(user2.getImgUrl()).into(winnerIV);
            Glide.with(this.requireActivity()).load(user1.getImgUrl()).into(loserIV);
            winner = "You Lose";
        }
        ((TextView)view.findViewById(R.id.winner)).setText(winner);

        return view;
    }
}