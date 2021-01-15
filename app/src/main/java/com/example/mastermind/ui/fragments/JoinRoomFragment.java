package com.example.mastermind.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.listeners.MethodCallBack;

public class JoinRoomFragment extends Fragment {

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_join_room, container, false);
        final EditText roomCodeEt = view.findViewById(R.id.roomCodeEt);
        view.findViewById(R.id.joinRoomBtn).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String currCode = roomCodeEt.getText().toString();
                 if (currCode.matches(""))
                     Toast.makeText(requireActivity(), "Please Type Game Code", Toast.LENGTH_SHORT).show();
                 else if(currCode.length() < 4 || currCode.length() > 5)
                     Toast.makeText(requireActivity(), "Please Type a Valid Game Code", Toast.LENGTH_SHORT).show();
                 else {
                     MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
                     methodCallBack.onCallBack(Const.ACTION_JOIN_ROOM, currCode);
                 }
             }
         });
        view.findViewById(R.id.createRoomBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
                methodCallBack.onCallBack(Const.ACTION_CREATE_ROOM, null);
            }
        });
        return view;
    }
}