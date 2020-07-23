package com.example.mastermind.model.game;

import android.net.Uri;

public class Record {
    private long time;
    private String name;
    private Uri imgUri;

    public Record() {
    }

    public Record(long time, String name, Uri imgUri) {
        this.time = time;
        this.name = name;
        this.imgUri = imgUri;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImgUri() {
        return imgUri;
    }

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }
}
