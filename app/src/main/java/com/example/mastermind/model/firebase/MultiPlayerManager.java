package com.example.mastermind.model.firebase;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mastermind.model.Const;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.user.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Random;

public class MultiPlayerManager implements Serializable {

    protected String code;
    protected boolean codeCreated = false;
    protected String player;
    protected String playerTurn = Const.PLAYER1_IN_FIREBASE;
    protected String userHidden = Const.NULL_ROW_IN_FIREBASE;
    protected String opponentHidden = Const.NULL_ROW_IN_FIREBASE;
    Activity context;
    protected boolean done;
    protected String opponent;
    protected String hidden;

    public MultiPlayerManager(Activity context) {
        this.code = "";
        this.context = context;
        done = false;
    }

    public MultiPlayerManager(Activity context, String code, String player1) {
        this.code = code;
        this.context = context;
        done = false;
        if (player1.equals(CurrentUser.getInstance().getId())) {
            player = Const.PLAYER1_IN_FIREBASE;
            opponent = Const.PLAYER2_IN_FIREBASE;
        } else {
            player = Const.PLAYER2_IN_FIREBASE;
            opponent = Const.PLAYER1_IN_FIREBASE;
        }
    }

    public void createRoom() {
        final String currCode = createStringCode();
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(currCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !codeCreated)
                    createRoom();
                else {
                    code = currCode;
                    player = Const.PLAYER1_IN_FIREBASE;
                    opponent = Const.PLAYER2_IN_FIREBASE;
                    if (!done) {
                        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.WHOS_TURN_IN_FIREBASE).setValue(Const.PLAYER1_IN_FIREBASE);
                        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.WINNER_IN_FIREBASE).setValue(Const.NONE_IN_FIREBASE);
                        done = true;
                    }
                    FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(player).setValue(CurrentUser.getInstance());
                    MethodCallBack methodCallBack = (MethodCallBack) context;
                    methodCallBack.onCallBack(2, null);
                    codeCreated = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void setHiddenInFirebase(String hidden) {
        this.hidden = hidden;
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.GAME_IN_FIREBASE).child(player + Const.HIDDEN_IN_FIREBASE).setValue(hidden);
        String other = Const.PLAYER1_IN_FIREBASE;
        if (player.equals(Const.PLAYER1_IN_FIREBASE))
            other = Const.PLAYER2_IN_FIREBASE;

        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.GAME_IN_FIREBASE).child(other + Const.HIDDEN_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MethodCallBack methodCallBack = (MethodCallBack) context;
                methodCallBack.onCallBack(8, player);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void retrieveHidden() {
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.GAME_IN_FIREBASE).child(player + Const.HIDDEN_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    userHidden = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        String other = Const.PLAYER1_IN_FIREBASE;
        if (player.equals(Const.PLAYER1_IN_FIREBASE))
            other = Const.PLAYER2_IN_FIREBASE;
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.GAME_IN_FIREBASE).child(other + Const.HIDDEN_IN_FIREBASE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    opponentHidden = snapshot.getValue(String.class);
                    MethodCallBack methodCallBack = (MethodCallBack) context;
                    methodCallBack.onCallBack(11, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    protected String createStringCode() {
        StringBuilder code = new StringBuilder();
        int limit;
        if (new Random().nextInt(2) == 0)
            limit = 4;
        else
            limit = 5;
        for (int i = 0; i < limit; i++)
            code.append(new Random().nextInt(10));
        return code.toString();
    }

    public void joinRoom(final String currCode) {
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(currCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(context, "Please Type a Valid Game Code", Toast.LENGTH_SHORT).show();
                } else {
                    setCode(currCode);
                    player = Const.PLAYER2_IN_FIREBASE;
                    opponent = Const.PLAYER1_IN_FIREBASE;
                    FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(player).setValue(CurrentUser.getInstance());
                    MethodCallBack methodCallBack = (MethodCallBack) context;
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

    public void addUserPeg(String row, String turn) {
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.GAME_IN_FIREBASE).child(Const.TURNS_IN_FIREBASE).child(turn).child(player).setValue(row);
    }

    public void turnRotation() {
        if (playerTurn.equals(Const.PLAYER1_IN_FIREBASE)) {
            playerTurn = Const.PLAYER2_IN_FIREBASE;
            FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.WHOS_TURN_IN_FIREBASE).setValue(Const.PLAYER2_IN_FIREBASE);
        } else {
            playerTurn = Const.PLAYER1_IN_FIREBASE;
            FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.WHOS_TURN_IN_FIREBASE).setValue(Const.PLAYER1_IN_FIREBASE);
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
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).child(Const.WINNER_IN_FIREBASE).setValue(this.player);
    }

    public void deleteRoom() {
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(code).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }
}
