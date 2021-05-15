package com.example.mastermind.model.user;

import androidx.annotation.NonNull;

import com.example.mastermind.model.Const;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentUser {

    private static User instance = null;
    private static int userCoins;
    private static boolean isNotified = false;

    private CurrentUser() {
    }

    public static User getInstance() {
        if (instance == null)
            instance = new User();
        try {
            instance.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            instance.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            instance.setImgUrl(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            instance.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(instance.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child(Const.COLLECTION_IN_FIREBASE).exists()) {
                        FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(instance.getId()).child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).setValue(0);
                        userCoins = 0;
                    } else
                        userCoins = snapshot.child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).getValue(Integer.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static void addCoins(int coins){
        userCoins += coins;
        FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(instance.getId()).child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).setValue(userCoins);
    }

    public static void addCoinsNotification() {
        if (!isNotified) {
            FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(instance.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int tAmount = Const.NOTIFICATION_EARN;
                    if (!snapshot.child(Const.COLLECTION_IN_FIREBASE).exists())
                        FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(instance.getId()).child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).setValue(0);
                    else
                        tAmount += snapshot.child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).getValue(Integer.class);
                    FirebaseDatabase.getInstance().getReference().child(Const.USERS_IN_FIREBASE).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Const.COLLECTION_IN_FIREBASE).child(Const.COINS_IN_FIREBASE).setValue(tAmount);
                    isNotified = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public static void resetNotification(){
        isNotified = false;
    }

    public static void setUserCoins(int userCoins) {
        CurrentUser.userCoins = userCoins;
    }

    public static int getUserCoins() {
        return userCoins;
    }

    public static void logout() {
        instance = null;
    }

}
