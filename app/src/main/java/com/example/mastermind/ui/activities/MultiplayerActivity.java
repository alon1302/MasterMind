package com.example.mastermind.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

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
    private int winner;
    String winnerString = "";
    private boolean isWaitingForWin = false;

    boolean allow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        winner = -1;
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
        Log.d(TAG, "createWinnerListener: "+ multiPlayerManager.getCode()+"      ()()()()()()()()()()()()()(");
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(multiPlayerManager.getCode()).child("Winner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: 0000000000000000000000000000000000000000000000");
                if (snapshot.exists()){
                    winnerString = snapshot.getValue(String.class);
                    String currPlayer = multiPlayerManager.getPlayer();
                    if (winnerString.equals("Player2")){
                        if (!isWaitingForWin) {
                            if (currPlayer.equals("Player2"))
                                winner = 1;
                            else
                                winner = 2;
                        }
                        else
                            winner = 0;
                        toEndGameFragment();
                    }
                    else if(winnerString.equals("Player1")){
                        isWaitingForWin = true;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void checkWinner(){
        if (winner != 0) {
            if (isWaitingForWin && multiPlayerManager.getPlayerTurn().equals("Player1")) {
                if (multiPlayerManager.getPlayer().equals("Player2"))
                    winner = 2;
                else
                    winner = 1;
                toEndGameFragment();
            }
        }
    }

    private void toEndGameFragment() {
        allow = false;
        Log.d(TAG, "onDataChange: ________" + winner);
        Bundle bundle = new Bundle();
        bundle.putInt("whoIsWin", winner);
        bundle.putSerializable("user1", user1);
        bundle.putSerializable("user2", user2);
        endGameFragment.setArguments(bundle);
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
        if(allow) {
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, userTurnFragment).commit();
        }
        checkWinner();
    }
    public void toOpponentFragment() {
        if (allow) {
            SendHiddenToOpponent sendHiddenToOpponent = (SendHiddenToOpponent) opponentTurnFragment;
            sendHiddenToOpponent.sendHidden(multiPlayerManager.getHidden());
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, opponentTurnFragment).commit();
        }
        checkWinner();
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
            createWinnerListener();
        }
        if (action == 4){
            choosed= true;
            d.show();
            multiPlayerManager.setHiddenInFirebase((String) value);
            multiPlayerManager.retrieveHidden();
            //multiPlayerManager.howsTurn();
        }
        if (action == 5) {
            if (winner == -1)
                toUserFragment();
        }
        if (action == 6) {
            Log.d(TAG, "onCallBack: " + isWaitingForWin + "_______________________");
            if (winner == -1)
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