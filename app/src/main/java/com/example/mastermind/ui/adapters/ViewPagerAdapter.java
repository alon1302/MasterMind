package com.example.mastermind.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mastermind.ui.fragments.AboutFragment;
import com.example.mastermind.ui.fragments.GoalFragment;
import com.example.mastermind.ui.fragments.RulesFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new AboutFragment();
            case 1:
                return new RulesFragment();
            case 2:
                return new GoalFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
