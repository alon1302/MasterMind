package com.example.mastermind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mastermind.MainActivity;
import com.example.mastermind.R;
import com.example.mastermind.ui.adapters.ViewPagerAdapter;
import com.example.mastermind.ui.fragments.AboutFragment;
import com.example.mastermind.ui.fragments.ChooseHiddenFragment;
import com.example.mastermind.ui.fragments.GoalFragment;
import com.example.mastermind.ui.fragments.RulesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HowToPlayActivity extends AppCompatActivity {

    ChipNavigationBar bottomNav;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    private MenuItem prevMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();

        viewPager = findViewById(R.id.fragment_container);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position){
                    case 0:
                        bottomNav.setItemSelected(R.id.nav_about, true);
                        break;
                    case 1:
                        bottomNav.setItemSelected(R.id.nav_rules, true);
                        break;
                    case 2:
                        bottomNav.setItemSelected(R.id.nav_goal, true);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("!!!!!!!!!", "onPageSelected: ");
                //if (prevMenuItem != null)
                    //prevMenuItem.setChecked(false);
//                else
//                    bottomNav.getMenu().getItem(0).setChecked(false);
//                if (position < 4) {
//                switch (position){
//                    case 0:
//                        bottomNav.setItemSelected(R.id.nav_about, true);
//                        break;
//                    case 1:
//                        bottomNav.setItemSelected(R.id.nav_rules, true);
//                        break;
//                    case 2:
//                        bottomNav.setItemSelected(R.id.nav_goal, true);
//                        break;
//                }

                  //prevMenuItem = bottomNav.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
//        bottomNav.setItemSelected(R.id.nav_about, true);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new AboutFragment());
        adapter.addFragment(new RulesFragment());
        adapter.addFragment(new GoalFragment());
        viewPager.setAdapter(adapter);
    }


    public void onClickStart(View view) {
        Intent intent;
        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
            //intent = new Intent(this, HomeActivity.class);
        //else
            //intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
    }

    private ChipNavigationBar.OnItemSelectedListener navListener =
            new ChipNavigationBar.OnItemSelectedListener(){
                @Override
                public void onItemSelected(int id) {

                    switch (id) {
                        case R.id.nav_about:
                            bottomNav.setItemSelected(0,true);
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.nav_rules:
                            bottomNav.setItemSelected(1,true);
                            viewPager.setCurrentItem(1);
                            break;
                        case R.id.nav_goal:
                            bottomNav.setItemSelected(2,true);
                            viewPager.setCurrentItem(2);
                            break;
                    }
                }
            };
}


