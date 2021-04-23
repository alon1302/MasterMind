package com.example.mastermind.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.listeners.SendUsersCallBack;
import com.example.mastermind.model.user.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class WaitingForOpponentFragment extends Fragment {


    private  String code;
    private User user1;
    private User user2;
    Activity thisActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        code = bundle.getString(Const.INTENT_EXTRA_KEY_CODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_waiting_for_opponent, container, false);
        ((TextView)(view.findViewById(R.id.codeTv))).setText("Game Code: " + code);
        this.thisActivity = requireActivity();

        if (getArguments()!= null && getArguments().containsKey(Const.INTENT_EXTRA_KEY_TYPE) && !getArguments().getBoolean(Const.INTENT_EXTRA_KEY_TYPE))
            ((TextView)(view.findViewById(R.id.codeTv))).setText("Searching Opponent...");



        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.PLAYER1_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        user1 = snapshot.getValue(User.class);
                        Glide.with(thisActivity).load(user1.getImgUrl()).into((CircleImageView) (view.findViewById(R.id.PlayerOne_image)));
                        if (user1 != null && user2 != null){
                            ((TextView)(view.findViewById(R.id.codeTv))).setText("Starting Game");
                            MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
                            methodCallBack.onCallBack(Const.ACTION_CHOOSE_HIDDEN, null);
                            SendUsersCallBack sendUsersCallBack = (SendUsersCallBack)requireActivity();
                            sendUsersCallBack.sendUsers(user1, user2);
                        }
                    } catch (Exception ignored) {}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.PLAYER2_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        user2 = snapshot.getValue(User.class);
                        Glide.with(thisActivity).load(user2.getImgUrl()).into((CircleImageView)(view.findViewById(R.id.PlayerTwo_image)));
                        if (user1 != null && user2 != null){
                            ((TextView)(view.findViewById(R.id.codeTv))).setText("Starting Game");
                            MethodCallBack methodCallBack = (MethodCallBack)requireActivity();
                            methodCallBack.onCallBack(Const.ACTION_CHOOSE_HIDDEN, null);
                            SendUsersCallBack sendUsersCallBack = (SendUsersCallBack)requireActivity();
                            sendUsersCallBack.sendUsers(user1, user2);
                        }
                    } catch (Exception ignored){}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }
}