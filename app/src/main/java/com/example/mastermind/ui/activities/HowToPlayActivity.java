package com.example.mastermind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mastermind.R;
import com.example.mastermind.ui.adapters.ViewPagerAdapter;
import com.example.mastermind.ui.fragments.AboutFragment;
import com.example.mastermind.ui.fragments.GoalFragment;
import com.example.mastermind.ui.fragments.RulesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class HowToPlayActivity extends AppCompatActivity {

    TabLayout bottomNav;
    ViewPager2 viewPager;
    ViewPagerAdapter adapter;
    private MenuItem prevMenuItem;
    String[] titles = {"About", "Rules", "Goal"};
    int[] icons = {R.drawable.about, R.drawable.rules, R.drawable.target};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        bottomNav = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.fragment_container);
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(bottomNav, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
                tab.setIcon(icons[position]);
            }
        }).attach();
    }


    public void onClickStart(View view) {
        finish();
    }

}


