package com.example.mastermind.model.serviceAndBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.mastermind.model.Const;
import com.example.mastermind.model.listeners.MethodCallBack;

public class NetworkChangeReceiver extends BroadcastReceiver {

    Context context;

    public NetworkChangeReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try{
            MethodCallBack methodCallBack = (MethodCallBack) this.context;
            if (isOnline(context))
                methodCallBack.onCallBack(Const.ONLINE,null);
            else
                methodCallBack.onCallBack(Const.OFFLINE,null);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && (networkInfo.isConnected() || networkInfo.isAvailable());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}