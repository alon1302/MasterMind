package com.example.mastermind.model.firebase;

import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.ui.activities.MultiplayerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

import java.io.Serializable;
import java.util.Random;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MultiPlayerManager implements Serializable {

    protected String code;
    protected boolean codeCreated = false;
    protected String TAG = "MultiPlayerManager";
    protected String player;
    protected String playerTurn="Player1";
    protected String userHidden = "nnnn";
    protected String opponentHidden="nnnn";
    Activity context;
    protected boolean done;
    protected String opponent;
    protected String hidden;

    public MultiPlayerManager(Activity context) {
        this.code = "";
        this.context = context;
        done = false;
    }
    public MultiPlayerManager(Activity context,String code, String player1) {
        this.code = code;
        this.context = context;
        done = false;
        if (player1.equals(CurrentUser.getInstance().getId())){
            player = "Player1";
            opponent = "Player2";
        }else {
            player = "Player2";
            opponent = "Player1";
        }
    }

    public void createRoom(){
        final String currCode = createStringCode();
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(currCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && !codeCreated){
                    createRoom();
                }
                else{
                    code = currCode;
                    player = "Player1";
                    opponent = "Player2";
                    if (!done) {
                        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("HowsTurn").setValue("Player1");
                        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Winner").setValue("None");
                        done = true;
                    }
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child(player).setValue(CurrentUser.getInstance());
                    MethodCallBack methodCallBack = (MethodCallBack)context;
                    methodCallBack.onCallBack(2, null);
                    codeCreated = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setHiddenInFirebase(String hidden){
        this.hidden = hidden;
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").child(player + "Hidden").setValue(hidden);
        String other = "Player1";
        if (player.equals("Player1"))
            other = "Player2";

        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").child(other+"Hidden").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MethodCallBack methodCallBack = (MethodCallBack)context;
                methodCallBack.onCallBack(8, player);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void retrieveHidden(){
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").child(player + "Hidden").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    userHidden = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String other = "Player1";
        if (player.equals("Player1"))
            other = "Player2";
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").child(other + "Hidden").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    opponentHidden = snapshot.getValue(String.class);
                    MethodCallBack methodCallBack = (MethodCallBack) context;
                    methodCallBack.onCallBack(11,null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    protected String createStringCode(){
        StringBuilder code = new StringBuilder();
        int limit;
        if (new Random().nextInt(2) == 0)
            limit = 4;
        else
            limit = 5;
        for (int i=0; i< limit; i++)
            code.append(new Random().nextInt(10));
        return code.toString();
    }

    public void joinRoom(final String currCode){
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(currCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Toast.makeText(context, "Please Type a Valid Game Code", Toast.LENGTH_SHORT).show();
                }
                else{
                    setCode(currCode);
                    player = "Player2";
                    opponent = "Player2";
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child(player).setValue(CurrentUser.getInstance());
                    MethodCallBack methodCallBack = (MethodCallBack)context;
                    methodCallBack.onCallBack(2, null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void addUserPeg(String row,String turn){
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").child("Turns").child(turn).child(player).setValue(row);
    }

    public void turnRotation(){
        if (playerTurn.equals("Player1")){
            playerTurn = "Player2";
            FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("HowsTurn").setValue("Player2");
        }
        else {
            playerTurn = "Player1";
            FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("HowsTurn").setValue("Player1");
        }

    }

    public void setPlayerTurn(String playerTurn) {
        this.playerTurn = playerTurn;
    }

    public String getPlayer() {
        return player;
    }

    public String getPlayerTurn() {
        return playerTurn;
    }

    public String getUserHidden() {
        return userHidden;
    }

    public String getOpponentHidden() {
        return opponentHidden;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponentHidden(String opponentHidden) {
        this.opponentHidden = opponentHidden;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getHidden() {
        return hidden;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    public void setWinnerInFirebase() {
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Winner").setValue(this.player);
    }

    public void deleteRoom() {
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }
}
