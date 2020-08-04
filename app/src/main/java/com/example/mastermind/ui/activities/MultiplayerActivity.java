package com.example.mastermind.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.mastermind.R;
import com.example.mastermind.model.firebase.MultiPlayerManager;
import com.example.mastermind.model.listeners.MethodCallBack;
import com.example.mastermind.ui.fragments.AboutFragment;
import com.example.mastermind.ui.fragments.ChooseHiddenFragment;
import com.example.mastermind.ui.fragments.JoinRoomFragment;
import com.example.mastermind.ui.fragments.WaitingForOpponentFragment;

public class MultiplayerActivity extends AppCompatActivity implements MethodCallBack {

    private static final String TAG = "Multiplayer" ;
    MultiPlayerManager multiPlayerManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        multiPlayerManager = new MultiPlayerManager();

        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, new JoinRoomFragment()).commit();

    }

    public void toWaitingFragment(){
        WaitingForOpponentFragment waitingForOpponentFragment =  new WaitingForOpponentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", multiPlayerManager.getCode());
        waitingForOpponentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.multiplayer_container, waitingForOpponentFragment).commit();
    }

    @Override
    public void onCallBack(int action, String value) {
        // 0 - create room
        // 1 - join room
        // 2 - room created
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
    }
}