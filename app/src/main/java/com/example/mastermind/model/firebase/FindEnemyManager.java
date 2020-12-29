package com.example.mastermind.model.firebase;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.user.CurrentUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindEnemyManager extends MultiPlayerManager{

    public FindEnemyManager(Activity context) {
        super(context);
    }

    @Override
    public void createRoom() {
        final String currCode = super.createStringCode();
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(currCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && !FindEnemyManager.super.codeCreated){
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
                    FirebaseDatabase.getInstance().getReference().child("AvailableRooms").child(code).setValue(code);
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

    public void joinRoom() {
        FirebaseDatabase.getInstance().getReference().child("AvailableRooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String code = ds.getValue(String.class);
                        FindEnemyManager.super.joinRoom(code);
                        ds.getRef().removeValue();
                        return;
                    }
                }
                else{
                    createRoom();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void deleteRoom() {
        super.deleteRoom();
        FirebaseDatabase.getInstance().getReference().child("AvailableRooms").child(code).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }
}
