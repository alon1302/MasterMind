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

    private CurrentUser() {
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
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
//            FirebaseDatabase.getInstance().getReference().child("Users/" + instance.getId()+ "/Collection/Coins").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    userCoins = snapshot.getValue(Integer.class);
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                }
//            });
        }
        return instance;
    }

    public static void addCoins(int coins){
        FirebaseDatabase.getInstance().getReference().child("Users/" + instance.getId()+ "/Collection/Coins").setValue(userCoins + coins);
        userCoins = userCoins + coins;
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
