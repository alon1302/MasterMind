package com.example.mastermind.model;

import android.graphics.drawable.Drawable;

public class Theme {

    private int pegImage;
    private boolean opened;

    public Theme(int pegImage, boolean opened) {
        this.pegImage = pegImage;
        this.opened = opened;
    }

    public int getPegImage() {
        return pegImage;
    }

    public void setPegImage(int pegImage) {
        this.pegImage = pegImage;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    @Override
    public String toString() {
        return "Theme{" +
                "pegImage=" + pegImage +
                ", opened=" + opened +
                '}';
    }
}
