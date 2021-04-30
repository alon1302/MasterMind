package com.example.mastermind.ui.activities;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.model.listeners.OnPegClickListener;
import com.example.mastermind.model.listeners.SendHiddenToOpponent;
import com.example.mastermind.model.listeners.SendUsersCallBack;
import com.example.mastermind.model.multiGame.FindEnemyManager;
import com.example.mastermind.model.multiGame.MultiPlayerManager;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.fragments.ChooseHiddenFragment;
import com.example.mastermind.ui.fragments.EndGameFragment;
import com.example.mastermind.ui.fragments.JoinRoomFragment;
import com.example.mastermind.ui.fragments.OpponentTurnFragment;
import com.example.mastermind.ui.fragments.UserTurnFragment;
import com.example.mastermind.ui.fragments.WaitingForOpponentFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MultiplayerActivity extends AppCompatActivity implements MethodCallBack, OnPegClickListener, SendUsersCallBack {

    public static final int GAME_NOT_OVER = -1;
    MultiPlayerManager multiPlayerManager;
    private UserTurnFragment userTurnFragment;
    private OpponentTurnFragment opponentTurnFragment;
    EndGameFragment endGameFragment;
    private boolean entered = false;
    private boolean entered2 = false;
    User user1, user2;
    Dialog d;
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
        d.setContentView(R.layout.waiting_dialog);
        d.setCancelable(false);
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        winner = GAME_NOT_OVER;
        if (getIntent().getStringExtra(Const.INTENT_EXTRA_KEY_CODE) != null) {
            user1 = (User) getIntent().getSerializableExtra(Const.INTENT_EXTRA_KEY_PLAYER1);
            user2 = (User) getIntent().getSerializableExtra(Const.INTENT_EXTRA_KEY_PLAYER2);
            multiPlayerManager = new MultiPlayerManager(this, getIntent().getStringExtra(Const.INTENT_EXTRA_KEY_CODE), user1.getId());
            sendUsers(user1, user2);
            toChooseFragment();
            FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(multiPlayerManager.getCode()).child(Const.REMATCH_IN_FIREBASE).removeValue();
            createWinnerListener();
        } else if (getIntent().getStringExtra(Const.INTENT_EXTRA_KEY_TYPE).equals(Const.INTENT_EXTRA_VALUE_WITH_CODE)) {
            multiPlayerManager = new MultiPlayerManager(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, new JoinRoomFragment()).commit();
        } else if (getIntent().getStringExtra(Const.INTENT_EXTRA_KEY_TYPE).equals(Const.INTENT_EXTRA_VALUE_FIND_ENEMY)) {
            FindEnemyManager findEnemyManager = new FindEnemyManager(this);
            findEnemyManager.joinRoom();
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, new ChooseHiddenFragment()).commit();
            this.multiPlayerManager = (MultiPlayerManager) findEnemyManager;
        }
        endGameFragment = new EndGameFragment();
    }

    ValueEventListener winnerListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                winnerString = snapshot.getValue(String.class);
                String currPlayer = multiPlayerManager.getPlayer();
                if (winnerString.equals(Const.PLAYER2)) {
                    if (!isWaitingForWin) {
                        if (currPlayer.equals(Const.PLAYER2))
                            winner = Const.END_GAME_SITUATION_WIN;
                        else
                            winner = Const.END_GAME_SITUATION_LOSE;
                    } else
                        winner = Const.END_GAME_SITUATION_TIE;
                    toEndGameFragment();
                } else if (winnerString.equals(Const.PLAYER1))
                    isWaitingForWin = true;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    public void createWinnerListener() {
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(multiPlayerManager.getCode()).child(Const.WINNER_IN_FIREBASE).addValueEventListener(winnerListener);
    }

    public void checkWinner() {
        if (winner != Const.END_GAME_SITUATION_TIE) {
            if (isWaitingForWin && multiPlayerManager.getPlayerTurn().equals(Const.PLAYER1)) {
                if (multiPlayerManager.getPlayer().equals(Const.PLAYER2))
                    winner = Const.END_GAME_SITUATION_LOSE;
                else
                    winner = Const.END_GAME_SITUATION_WIN;
                toEndGameFragment();
            }
        }
    }


    private void toEndGameFragment() {
        endGameFragment = new EndGameFragment();
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(multiPlayerManager.getCode()).child(Const.WINNER_IN_FIREBASE).removeEventListener(winnerListener);
        allow = false;
        Bundle bundle = new Bundle();
        bundle.putInt(Const.INTENT_EXTRA_KEY_WHO_IS_WIN, winner);
        bundle.putString(Const.INTENT_EXTRA_KEY_CODE, multiPlayerManager.getCode());
        bundle.putSerializable(Const.INTENT_EXTRA_KEY_USER1, user1);
        bundle.putSerializable(Const.INTENT_EXTRA_KEY_USER2, user2);
        bundle.putString(Const.INTENT_EXTRA_KEY_PLAYER, multiPlayerManager.getPlayer());
        endGameFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, endGameFragment).commitAllowingStateLoss();
    }

    public void toWaitingFragment() {
        WaitingForOpponentFragment waitingForOpponentFragment = new WaitingForOpponentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Const.INTENT_EXTRA_KEY_CODE, multiPlayerManager.getCode());
        if (getIntent().getStringExtra(Const.INTENT_EXTRA_KEY_TYPE).equals(Const.INTENT_EXTRA_VALUE_FIND_ENEMY)){
            bundle.putBoolean(Const.INTENT_EXTRA_KEY_TYPE,false);
        }
        waitingForOpponentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, waitingForOpponentFragment).commit();
    }

    public void toChooseFragment() {
        if (!entered) {
            ChooseHiddenFragment chooseHiddenFragment = new ChooseHiddenFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Const.INTENT_EXTRA_KEY_CODE, multiPlayerManager.getCode());
            chooseHiddenFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, chooseHiddenFragment).commit();
            entered = true;
        }
    }

    public void toUserFragment() {
        if (allow)
            getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, userTurnFragment).commit();
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
        if (action == Const.ACTION_CREATE_ROOM) {
            multiPlayerManager.createRoom();
        } else if (action == Const.ACTION_JOIN_ROOM) {
            multiPlayerManager.joinRoom((String) value);
        } else if (action == Const.ACTION_ROOM_CREATED) {
            if (!entered2) {
                toWaitingFragment();
                entered2 = true;
            }
        } else if (action == Const.ACTION_CHOOSE_HIDDEN) {
            toChooseFragment();
            createWinnerListener();
        } else if (action == Const.ACTION_HIDDEN_TO_FIREBASE) {
            choosed = true;
            d.show();
            multiPlayerManager.setHiddenInFirebase((String) value);
            multiPlayerManager.retrieveHidden();
        } else if (action == Const.ACTION_TO_USER_TURN) {
            if (winner == GAME_NOT_OVER)
                toUserFragment();
        } else if (action == Const.ACTION_TO_OPPONENT_TURN) {
            if (winner == GAME_NOT_OVER)
                toOpponentFragment();
        } else if (action == Const.ACTION_UPLOAD_ROW_CHANGES) {
            String row = ((String) value).substring(0, 4);
            String turn = ((String) value).substring(5);
            multiPlayerManager.addUserPeg(row, turn);
        } else if (action == Const.ACTION_START_GAME_AFTER_CHOOSE) {
            if (value.equals(Const.PLAYER1))
                toUserFragment();
            else
                toOpponentFragment();
        } else if (action == Const.ACTION_TURN_ROTATION) {
            multiPlayerManager.turnRotation();
        } else if (action == Const.ACTION_WAITING_TO_WIN) {
            multiPlayerManager.setWinnerInFirebase();
        } else if (action == Const.ACTION_DISMISS_WAITING_DIALOG) {
            d.dismiss();
        }
    }

    @Override
    public void onPositionClicked(int position) {
        OnPegClickListener onPegClickListener = (OnPegClickListener) userTurnFragment;
        onPegClickListener.onPositionClicked(position);
    }

    @Override
    public void sendUsers(User user1, User user2) {
        if (CurrentUser.getInstance().getId().equals(user1.getId())) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user2 = user1;
            this.user1 = user2;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Const.INTENT_EXTRA_KEY_PLAYER, multiPlayerManager.getPlayer());
        bundle.putString(Const.INTENT_EXTRA_KEY_OPPONENT, multiPlayerManager.getOpponent());
        bundle.putSerializable(Const.INTENT_EXTRA_KEY_USER1, this.user1);
        bundle.putSerializable(Const.INTENT_EXTRA_KEY_USER2, this.user2);
        bundle.putString(Const.INTENT_EXTRA_KEY_OPPONENT_HIDDEN, multiPlayerManager.getOpponentHidden());
        bundle.putString(Const.INTENT_EXTRA_KEY_CODE, multiPlayerManager.getCode());
        userTurnFragment.setArguments(bundle);
        opponentTurnFragment.setArguments(bundle);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String turn = snapshot.getValue(String.class);
                multiPlayerManager.setPlayerTurn(turn);
                if (choosed && turn != null) {
                    if (!turn.equals(multiPlayerManager.getPlayer()))
                        toOpponentFragment();
                    else
                        toUserFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        FirebaseDatabase.getInstance().getReference().child(Const.ROOMS_IN_FIREBASE).child(multiPlayerManager.getCode()).child(Const.WHOS_TURN_IN_FIREBASE).addValueEventListener(valueEventListener);
    }

    private void deleteFromFirebase() {
        if (multiPlayerManager instanceof FindEnemyManager) {
            FindEnemyManager findEnemyManager = (FindEnemyManager) multiPlayerManager;
            findEnemyManager.deleteRoom();
        } else
            multiPlayerManager.deleteRoom();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteFromFirebase();
    }
}


