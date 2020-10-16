package com.example.mastermind.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.model.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference mStorageRef;

    FirebaseDatabase database;
    DatabaseReference myRef;
    User user;
    TextView tv_name;
    TextView tv_coins;
    CircleImageView circleImageView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        user = CurrentUser.getInstance();

        Log.d("", "onCreate: " + user.toString());

        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(user.getName());
        showCoins();

        circleImageView = findViewById(R.id.iv_image);
        Glide.with(this).load(user.getImgUrl()).into(circleImageView);
    }

    public void showCoins(){
        tv_coins = findViewById(R.id.textView_coins);
        FirebaseDatabase.getInstance().getReference().child("Users/" + CurrentUser.getInstance().getId() + "/Collection/Coins").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int coins = snapshot.getValue(Integer.class);
                    tv_coins.setText("" + coins);
                    CurrentUser.setUserCoins(coins);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Users/" + CurrentUser.getInstance().getId() + "/Collection/Coins").setValue(0);
                    CurrentUser.setUserCoins(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onClickOnePlayer(View view) {
        Intent intent = new Intent(this, OnePlayerActivity.class);
        startActivity(intent);
    }

    public void onClickTwoPlayer(View view){
        Intent intent = new Intent(this, MultiplayerActivity.class);
        startActivity(intent);
    }

    public void onClickLogOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        mAuth.signOut();
        CurrentUser.logout();
        currentUser = null;
        startActivity(intent);
    }

    public void onClickHowToPlay(View view) {
        Intent intent = new Intent(this, HowToPlayActivity.class);
        startActivity(intent);
    }
}
