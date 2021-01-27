package com.example.mastermind.model.user;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentUser {

    private static User instance = null;
    private static int userCoins;
    private static int notification = 0;

    private CurrentUser() {
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
            instance.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            instance.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            instance.setImgUrl(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            instance.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            FirebaseDatabase.getInstance().getReference().child("Users/" + instance.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child("Collection").exists()){
                        FirebaseDatabase.getInstance().getReference().child("Users/" + instance.getId()+ "/Collection/Coins").setValue(0);
                        userCoins = 0;
                    }
                    else{
                        userCoins = snapshot.child("Collection/Coins").getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        return instance;
    }

    public static void addCoins(int coins){
        FirebaseDatabase.getInstance().getReference().child("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Collection/Coins").setValue(userCoins + coins);
        userCoins = userCoins + coins;
    }
    public static void addCoinsNotification(final int coins) {
        if (notification == 0) {
            FirebaseDatabase.getInstance().getReference()
                    .child("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int tAmount = coins;
                    if (!snapshot.child("Collection").exists()) {
                        FirebaseDatabase.getInstance().getReference().child("Users/" + instance.getId() + "/Collection/Coins").setValue(0);
                    } else {
                        tAmount += snapshot.child("Collection/Coins").getValue(Integer.class);
                    }
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Collection/Coins")
                            .setValue(tAmount);
                    notification = 1;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public static void resetNotification(){
        notification = 0;
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
