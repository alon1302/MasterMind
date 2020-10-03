package com.example.mastermind.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.mastermind.model.listeners.SendHiddenToOpponent;
import com.example.mastermind.model.listeners.SendUsersCallBack;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.fragments.EndGameFragment;
import com.example.mastermind.ui.fragments.OpponentTurnFragment;
import com.example.mastermind.R;
import com.example.mastermind.ui.fragments.UserTurnFragment;
import com.example.mastermind.model.firebase.MultiPlayerManager;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.ui.fragments.ChooseHiddenFragment;
import com.example.mastermind.ui.fragments.JoinRoomFragment;
import com.example.mastermind.ui.fragments.WaitingForOpponentFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class MultiplayerActivity extends AppCompatActivity implements MethodCallBack , OnPegClickListener, SendUsersCallBack {

    private static final String TAG = "Multiplayer" ;
    MultiPlayerManager multiPlayerManager;
    private UserTurnFragment userTurnFragment;
    private OpponentTurnFragment opponentTurnFragment;
    EndGameFragment endGameFragment;
    private boolean entered = false;
    private boolean entered2 = false;
    User user1, user2;
    Dialog d;
    private ValueEventListener valueEventListener;
    private boolean choosed;

    private boolean isWaitingForWin = false;
    boolean allow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        allow = true;
        userTurnFragment = new UserTurnFragment();
        opponentTurnFragment = new OpponentTurnFragment();
        endGameFragment = new EndGameFragment();
        choosed = false;
        multiPlayerManager = new MultiPlayerManager(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, new JoinRoomFragment()).commit();

        d = new Dialog(this);
        d.setContentView(R.layout.loading_dialog);
        d.setCancelable(false);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    public void createWinnerListener(){
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(multiPlayerManager.getCode()).child("Winner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String value = snapshot.getValue(String.class);
                    Log.d(TAG, "onDataChange: " + value + "4444444444444444444444444444444444444444444444444");
                    if (value.equals("Player1")){
                        if (isWaitingForWin) {
                            toEndGameFragment(1);
                            allow = false;
                        }
                        else
                            isWaitingForWin = true;
                    }
                    else /*if (value.equals("Player2"))*/ {
                        allow = false;
                        if (isWaitingForWin)
                            toEndGameFragment(0);
                        else
                            toEndGameFragment(2);
                    }
                }
                else{
                    Log.d(TAG, "onDataChange: "  + "4444444444444444444444444444444444444444444444444");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void toEndGameFragment(int whoIsWin) {
        Log.d(TAG, "toEndGameFragment: in 9999999999999999999999999999999999999999999999999999999999999999_____ " + whoIsWin);
        Bundle bundle = new Bundle();
        bundle.putInt("whoIsWin", whoIsWin);
        endGameFragment.setArguments(bundle);
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, endGameFragment).commit();
    }

    public void toWaitingFragment(){
        WaitingForOpponentFragment waitingForOpponentFragment =  new WaitingForOpponentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", multiPlayerManager.getCode());
        waitingForOpponentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, waitingForOpponentFragment).commit();
    }

    public void toChooseFragment(){
        Log.d(TAG, "toChooseFragment: " +  entered + "878787878787878787878");

        if (!entered) {
            ChooseHiddenFragment chooseHiddenFragment = new ChooseHiddenFragment();
            Bundle bundle = new Bundle();
            bundle.putString("code", multiPlayerManager.getCode());
            chooseHiddenFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, chooseHiddenFragment).commit();
            entered = true;
        }
    }
    public void toUserFragment(){
        Log.d(TAG, "toChooseFragment: 6666");
        if (allow)
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, userTurnFragment).commit();

    }
    public void toOpponentFragment(){
        Log.d(TAG, "toChooseFragment: 61123");
        if (allow) {
            SendHiddenToOpponent sendHiddenToOpponent = (SendHiddenToOpponent) opponentTurnFragment;
            sendHiddenToOpponent.sendHidden(multiPlayerManager.getHidden());
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, opponentTurnFragment).commit();
        }
    }

    @Override
    public void onCallBack(int action, Object value) {
        // 0 - create room
        // 1 - join room
        // 2 - room created
        // 3 - choose
        // 4 - add hidden to database
        // 5 - user
        // 6 - opponent
        // 7 - upload row changes
        // 8 - start game after choosing
        // 9 - turn rotation
        // 10 - waiting for last turn to win
        if (action == 0) {
            multiPlayerManager.createRoom();
            return;
        }
        if (action == 1 ){
            multiPlayerManager.joinRoom((String) value);
        }
        if (action == 2){
            if (!entered2) {
                toWaitingFragment();
                entered2 = true;
            }
        }
        if(action == 3){
            toChooseFragment();
        }
        if (action == 4){
            choosed= true;
            d.show();
            multiPlayerManager.setHiddenInFirebase((String) value);
            multiPlayerManager.retriveHiddens();
            //multiPlayerManager.howsTurn();
        }
        if (action == 5) {
            if (allow)
                toUserFragment();
        }
        if (action == 6) {
            if (allow)
                toOpponentFragment();
        }
        if (action == 7){
            String row = ((String) value).substring(0,4);
            String turn = ((String) value).substring(5);
            multiPlayerManager.addUserPeg(row,turn);
        }
        if (action == 8){
            if (value.equals("Player1"))
                toUserFragment();
            else
                toOpponentFragment();
            d.dismiss();
        }
        if (action == 9){
            multiPlayerManager.turnRotation();
        }
        if (action == 10){
            createWinnerListener();
            multiPlayerManager.setWinnerInFirebase();
        }
    }

    @Override
    public void onPositionClicked(int position) {
        OnPegClickListener onPegClickListener = (OnPegClickListener)userTurnFragment;
        onPegClickListener.onPositionClicked(position);
    }

    @Override
    public void sendUsers(User user1, User user2) {
        if (CurrentUser.getInstance().getId().equals(user1.getId())) {
            this.user1 = user1;
            this.user2 = user2;
        }
        else{
            this.user2 = user1;
            this.user1 = user2;
        }
        Bundle bundle = new Bundle();
        bundle.putString("player", multiPlayerManager.getPlayer());
        bundle.putString("opponent", multiPlayerManager.getOpponent());
        bundle.putSerializable("user1", this.user1);
        bundle.putSerializable("user2", this.user2);
        bundle.putString("opponentHidden",multiPlayerManager.getOpponentHidden());
        bundle.putString("code", multiPlayerManager.getCode());
        userTurnFragment.setArguments(bundle);
        opponentTurnFragment.setArguments(bundle);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String turn = snapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: ");
                multiPlayerManager.setPlayerTurn(turn);
                if (choosed) {
                    if (!turn.equals(multiPlayerManager.getPlayer())) {
                        toOpponentFragment();
                    } else {
                        toUserFragment();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(multiPlayerManager.getCode()).child("HowsTurn").addValueEventListener(valueEventListener);
    }
}