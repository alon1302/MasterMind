package com.example.mastermind.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.mastermind.R;
import com.example.mastermind.model.user.CurrentUser;

import java.util.ArrayList;

public class Themes {

    private static Themes instance = null;
    private ArrayList<Theme> allThemes = new ArrayList<>();
    private int usedTheme;
    private Context context;


    private Themes(){
    }

    public static Themes getInstance(Context context){
        if (instance == null){
            instance = new Themes();
            instance.setContext(context);
            instance.loadThemes();
        }
        return instance;
    }

    private void loadThemes(){
        allThemes.add(0, new Theme(R.color.transparent, true));
        allThemes.add(1, new Theme(R.drawable.smiley, false));
        allThemes.add(2, new Theme(R.drawable.basketball, false));
        allThemes.add(3, new Theme(R.drawable.football, false));
        allThemes.add(4, new Theme(R.drawable.star, false));
        allThemes.add(5, new Theme(R.drawable.pokeball, false));
        allThemes.add(6, new Theme(R.drawable.heart, false));
        allThemes.add(7, new Theme(R.drawable.flower, false));
        allThemes.add(8, new Theme(R.drawable.car, false));
        allThemes.add(9, new Theme(R.drawable.plane, false));

        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("ThemesPrefs:" + CurrentUser.getInstance().getId(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("" + 0, true);
        editor.putInt("index", 0);
        //allThemes.add(, new Theme(R.drawable.butterfly, false));
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<Theme> getAllThemes() {
        return allThemes;
    }

    public int getUsedTheme() {
        return usedTheme;
    }
}
