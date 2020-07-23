package com.example.mastermind.model.user;

import com.google.firebase.auth.FirebaseAuth;

public class CurrentUser {

    private static User instance = null;

    private CurrentUser() {
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
            instance.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            instance.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            instance.setImgUrl(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            instance.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
        return instance;
    }

    public static void logout() {
        instance = null;
    }

}
