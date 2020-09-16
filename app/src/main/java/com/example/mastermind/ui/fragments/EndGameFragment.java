package com.example.mastermind.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mastermind.R;


public class EndGameFragment extends Fragment {

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
        int situation = getArguments().getInt("whoIsWin");
        String winner = "";
        if (situation == 0)
            winner = "Its a Tie";
        else if (situation == 1)
            winner = "You Win";
        else
            winner = "You Lose";
        ((TextView)view.findViewById(R.id.winner)).setText(winner);
        return view;
    }
}