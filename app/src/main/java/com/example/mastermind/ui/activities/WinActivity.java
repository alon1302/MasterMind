package com.example.mastermind.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mastermind.R;
import com.example.mastermind.model.firebase.RecordPerUser;
import com.example.mastermind.model.firebase.RecordsRepo;
import com.example.mastermind.model.game.Record;
import com.example.mastermind.model.listeners.DataChangedListener;
import com.example.mastermind.model.user.CurrentUser;
import com.example.mastermind.model.user.User;
import com.example.mastermind.ui.adapters.AdapterRecords;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class WinActivity extends AppCompatActivity implements DataChangedListener {

    User currentUser;
    TextView tv_name;
    TextView tv_time;
    CircleImageView profileImage;
    long minutes, seconds;
    long time;

    AdapterRecords adapterRecords;
    ArrayList<RecordPerUser> records;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        currentUser = CurrentUser.getInstance();
        tv_name = findViewById(R.id.winner_name);
        tv_time = findViewById(R.id.winner_Time);
        profileImage = findViewById(R.id.profile_image);
        getData();

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        records = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_records);
        recyclerView.setLayoutManager(layoutManager);

        RecordsRepo.addRecord(new Record(time, CurrentUser.getInstance().getId()), this);
        records = RecordsRepo.getRecords(this);

        adapterRecords = new AdapterRecords(records, this);
        recyclerView.setAdapter(adapterRecords);
    }


    public void onClickRestart(View view) {
        Intent intent = new Intent(this, OnePlayerActivity.class);
        startActivity(intent);
        finish();
    }
    public void onClickHome(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void getData() {
        Intent intent = getIntent();
        minutes = intent.getLongExtra("minutes", 0);
        seconds = intent.getLongExtra("seconds", 0);
        time = intent.getLongExtra("time", 0);
        tv_name.setText(currentUser.getName());
        tv_time.setText(String.format(Locale.getDefault(),"%02d:%02d",minutes, seconds));
        Glide.with(this).load(currentUser.getImgUrl()).into(profileImage);
    }

    @Override
    public void onDataChanged() {
        adapterRecords.notifyDataSetChanged();
    }
}