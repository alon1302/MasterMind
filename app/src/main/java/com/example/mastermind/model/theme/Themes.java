package com.example.mastermind.model.theme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.mastermind.R;
import com.example.mastermind.model.Const;
import com.example.mastermind.model.user.CurrentUser;

import java.util.ArrayList;

public class Themes {

    private static Themes instance = null;
    private final ArrayList<Theme> allThemes = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int currentThemeIndex;

    private Themes() {
    }

    @SuppressLint("CommitPrefEdits")
    public static Themes getInstance(Context context) {
        if (instance == null) {
            instance = new Themes();
            instance.sharedPreferences = context.getApplicationContext().getSharedPreferences(Const.SHARED_PREFERENCES_ID + CurrentUser.getInstance().getId(), Context.MODE_PRIVATE);
            instance.editor = instance.sharedPreferences.edit();
            instance.loadThemes();
        }
        return instance;
    }

    private void loadThemes() {
        editor.putBoolean("" + 0, true);
        for (int i = 0; i < 10; i++)
            allThemes.add(i, getTheme(i, sharedPreferences.getBoolean("" + i, false)));
        if (!sharedPreferences.contains(Const.SHARED_PREFERENCES_KEY_INDEX))
            editor.putInt(Const.SHARED_PREFERENCES_KEY_INDEX, 0);
        currentThemeIndex = sharedPreferences.getInt(Const.SHARED_PREFERENCES_KEY_INDEX, 0);
        editor.apply();
    }

    public void openATheme(int index) {
        editor.putBoolean("" + index, true);
        editor.putInt(Const.SHARED_PREFERENCES_KEY_INDEX, index);
        getAllThemes().get(index).setOpened(true);
        currentThemeIndex = index;
        editor.apply();
    }

    public void setCurrentThemeIndex(int index){
        editor.putInt(Const.SHARED_PREFERENCES_KEY_INDEX, index);
        editor.apply();
        currentThemeIndex = index;
    }

    public ArrayList<Theme> getAllThemes() {
        return allThemes;
    }

    public int getCurrentThemeIndex() {
        return currentThemeIndex;
    }

    private Theme getTheme(int index, boolean isOpen) {
        switch (index) {
            default:
                return null;
            case 0:
                return new Theme(R.color.transparent, true);
            case 1:
                return new Theme(R.drawable.smiley, isOpen);
            case 2:
                return new Theme(R.drawable.basketball, isOpen);
            case 3:
                return new Theme(R.drawable.football, isOpen);
            case 4:
                return new Theme(R.drawable.star, isOpen);
            case 5:
                return new Theme(R.drawable.pokeball, isOpen);
            case 6:
                return new Theme(R.drawable.heart, isOpen);
            case 7:
                return new Theme(R.drawable.flower, isOpen);
            case 8:
                return new Theme(R.drawable.car, isOpen);
            case 9:
                return new Theme(R.drawable.plane, isOpen);
        }
    }

    public void logout(){
        instance = null;
    }
}
