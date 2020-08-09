package com.example.mastermind.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.mastermind.OpponentTurnFragment;
import com.example.mastermind.R;
import com.example.mastermind.UserTurnFragment;
import com.example.mastermind.model.firebase.MultiPlayerManager;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.ui.fragments.AboutFragment;
import com.example.mastermind.ui.fragments.ChooseHiddenFragment;
import com.example.mastermind.ui.fragments.JoinRoomFragment;
import com.example.mastermind.ui.fragments.WaitingForOpponentFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MultiplayerActivity extends AppCompatActivity implements MethodCallBack , OnPegClickListener {

    private static final String TAG = "Multiplayer" ;
    MultiPlayerManager multiPlayerManager;
    private UserTurnFragment userTurnFragment;
    private OpponentTurnFragment opponentTurnFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        userTurnFragment = new UserTurnFragment();
        opponentTurnFragment = new OpponentTurnFragment();
        multiPlayerManager = new MultiPlayerManager();
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, new JoinRoomFragment()).commit();

        FirebaseDatabase.getInstance().getReference().child("Rooms").child(multiPlayerManager.getCode()).child("Game").child("Turn").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getValue(String.class).equals(multiPlayerManager.getPlayer())) {
                        toUserFragment();
                    } else {
                        toOpponentFragment();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void toWaitingFragment(){
        WaitingForOpponentFragment waitingForOpponentFragment =  new WaitingForOpponentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", multiPlayerManager.getCode());
        waitingForOpponentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, waitingForOpponentFragment).commit();
    }

    public void toChooseFragment(){
        Log.d(TAG, "toChooseFragment: called");
        ChooseHiddenFragment chooseHiddenFragment = new ChooseHiddenFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", multiPlayerManager.getCode());
        chooseHiddenFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, chooseHiddenFragment).commit();
    }
    public void toUserFragment(){
        Log.d(TAG, "toChooseFragment: called");
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, userTurnFragment).commit();
    }
    public void toOpponentFragment(){
        Log.d(TAG, "toChooseFragment: called");
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, opponentTurnFragment).commit();
    }

    @Override
    public void onCallBack(int action, String value) {
        // 0 - create room
        // 1 - join room
        // 2 - room created
        // 3 - choose
        // 4 - add hidden to database
        // 5 - user
        // 6 - opponent
        if (action == 0) {
            Log.d(TAG, "onCallBack: called back");
            multiPlayerManager.createRoom(this);
            return;
        }
        if (action == 1 ){
            multiPlayerManager.joinRoom(value,this);
        }
        if (action == 2){
            toWaitingFragment();
        }
        if(action == 3){
            toChooseFragment();
        }
        if (action == 4){
            multiPlayerManager.setHiddenInFirebase(value);
        }
        if (action == 5){
            toUserFragment();
        }
        if (action == 6){
            toOpponentFragment();
        }
        if (action == 7){
            String row = value.substring(0,4);
            String turn = value.substring(5);
            multiPlayerManager.addUserPeg(row,turn);
        }

    }

    @Override
    public void onPositionClicked(int position) {
        OnPegClickListener onPegClickListener = (OnPegClickListener)userTurnFragment;
        onPegClickListener.onPositionClicked(position);
    }
}