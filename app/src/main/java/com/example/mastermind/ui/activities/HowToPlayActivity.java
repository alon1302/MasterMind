package com.example.mastermind.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.ui.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HowToPlayActivity extends AppCompatActivity {

    private final String[] titles = {Const.FRAGMENT_TITLE_ABOUT, Const.FRAGMENT_TITLE_RULES, Const.FRAGMENT_TITLE_GOAL};
    private final int[] icons = {R.drawable.about, R.drawable.rules, R.drawable.target};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        TabLayout bottomNav = findViewById(R.id.bottom_navigation);
        ViewPager2 viewPager = findViewById(R.id.fragment_container);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
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


