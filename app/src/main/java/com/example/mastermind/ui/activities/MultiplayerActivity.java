package com.example.mastermind.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mastermind.model.firebase.FindEnemyManager;
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
    private boolean isWaitingForWin;
    boolean allow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        isWaitingForWin = false;
        allow = true;
        userTurnFragment = new UserTurnFragment();
        opponentTurnFragment = new OpponentTurnFragment();
        choosed = false;

        d = new Dialog(this);
        d.setContentView(R.layout.wait_to_opponent_dialog);
        d.setCancelable(false);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        winner = -1;
        if(getIntent().getStringExtra("code") != null){
            user1 = (User)getIntent().getSerializableExtra("player1");
            user2 = (User)getIntent().getSerializableExtra("player2");
            multiPlayerManager = new MultiPlayerManager(this,getIntent().getStringExtra("code"),((User)getIntent().getSerializableExtra("player1")).getId());
            sendUsers(user1,user2);
            toChooseFragment();
            Log.d("", isWaitingForWin + ".................................................................");
            FirebaseDatabase.getInstance().getReference().child("Rooms").child(multiPlayerManager.getCode()).child("Rematch").removeValue();
            createWinnerListener();
        }
        else if (getIntent().getStringExtra("type").equals("withCode")){
            multiPlayerManager = new MultiPlayerManager(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, new JoinRoomFragment()).commit();
        }
        else if (getIntent().getStringExtra("type").equals("findEnemy")){
            FindEnemyManager findEnemyManager = new FindEnemyManager(this);
            findEnemyManager.joinRoom();
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, new ChooseHiddenFragment()).commit();
            this.multiPlayerManager = (MultiPlayerManager)findEnemyManager;
        }
        endGameFragment = new EndGameFragment();

    }

    ValueEventListener winnerListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
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
    };

    public void createWinnerListener(){
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(multiPlayerManager.getCode()).child("Winner").addValueEventListener(winnerListener);
    }

    public void checkWinner(){
        if (winner != 0) {
            if (isWaitingForWin && multiPlayerManager.getPlayerTurn().equals("Player1")) {
                Log.d(TAG, "checkWinner: " + isWaitingForWin + "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" );
                if (multiPlayerManager.getPlayer().equals("Player2"))
                    winner = 2;
                else
                    winner = 1;
                toEndGameFragment();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (multiPlayerManager instanceof FindEnemyManager){
            FindEnemyManager findEnemyManager = (FindEnemyManager)multiPlayerManager;
            findEnemyManager.deleteRoom();
        }
        else{
            multiPlayerManager.deleteRoom();
        }
    }

    private void toEndGameFragment() {
        endGameFragment = new EndGameFragment();
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(multiPlayerManager.getCode()).child("Winner").removeEventListener(winnerListener);
        allow = false;
        Log.d(TAG, "onDataChange: ________" + winner);
        Bundle bundle = new Bundle();
        bundle.putInt("whoIsWin", winner);
        bundle.putString("code", multiPlayerManager.getCode());
        bundle.putSerializable("user1", user1);
        bundle.putSerializable("user2", user2);
        bundle.putString("player" , multiPlayerManager.getPlayer());
        endGameFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, endGameFragment).commitAllowingStateLoss();
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
        // 11 - dismiss waiting dialog
        if (action == 0) {
            multiPlayerManager.createRoom();
            return;
        }
        else if (action == 1 ){
            multiPlayerManager.joinRoom((String) value);
        }
        else if (action == 2){
            if (!entered2) {
                toWaitingFragment();
                entered2 = true;
            }
        }
        else if(action == 3){
            toChooseFragment();
            createWinnerListener();
        }
        else if (action == 4){
            //d.show();
            choosed= true;
            d.show();
            multiPlayerManager.setHiddenInFirebase((String) value);
            multiPlayerManager.retrieveHidden();
            //multiPlayerManager.howsTurn();
        }
        else if (action == 5) {
            if (winner == -1)
                toUserFragment();
        }
        else if (action == 6) {
            Log.d(TAG, "onCallBack: " + isWaitingForWin + "_______________________");
            if (winner == -1)
                toOpponentFragment();
        }
        else if (action == 7){
            String row = ((String) value).substring(0,4);
            String turn = ((String) value).substring(5);
            multiPlayerManager.addUserPeg(row,turn);
        }
        else if (action == 8){
            if (value.equals("Player1"))
                toUserFragment();
            else
                toOpponentFragment();
            //d.dismiss();
        }
        else if (action == 9){
            multiPlayerManager.turnRotation();
        }
        else if (action == 10){
            multiPlayerManager.setWinnerInFirebase();
        }
        else if (action == 11){
            d.dismiss();
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
                if (choosed && turn!=null) {
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

    @Override
    protected void onStop() {
        super.onStop();

    }
}


