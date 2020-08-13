package com.example.mastermind.model.firebase;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.user.CurrentUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

import java.util.Random;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MultiPlayerManager {

    private String code;
    private boolean codeCreated = false;
    private String TAG = "MultiPlayerManager";
    private String player;
    private String playerTurn = "Player1";
    private String userHidden = "nnnn";
    private String opponentHidden="nnnn";
    Activity context;

    public MultiPlayerManager() {
        this.code = "";
    }

    public void createRoom(final Activity context){
        this.context = context;
        final String currCode = createStringCode();
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(currCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && !codeCreated){
                    createRoom(context);
                }
                else{
                    code = currCode;
                    player = "Player1";
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

    public void retriveHiddens(){
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
                if (snapshot.exists())
                    opponentHidden = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private String createStringCode(){
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

    public void joinRoom(final String currCode, final Activity context){
        this.context = context;
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(currCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    Toast.makeText(context, "Please Type a Valid Game Code", Toast.LENGTH_SHORT).show();
                }
                else{
                    code =currCode;
                    player = "Player2";
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
        }
        if (playerTurn.equals("Player2")){
            playerTurn = "Player1";
        }
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Game").child("Turn").setValue(playerTurn);
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
}
