package com.example.mastermind.model.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mastermind.model.user.CurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MultiPlayerManager {

    private String code;
    private boolean codeCreated = false;
    private String TAG = "MultiPlayerManager";

    public void createRoom(){
        final String currCode = createStringCode();
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(currCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && !codeCreated){
                    createRoom();
                }
                else{
                    code = currCode;
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Player1").setValue(CurrentUser.getInstance().getId());
                    codeCreated = true;
                }
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

    public void joinRoom(final String code){
        FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    // TODO toast (code not exist)
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Rooms").child(code).child("Player2").setValue(CurrentUser.getInstance().getId());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
