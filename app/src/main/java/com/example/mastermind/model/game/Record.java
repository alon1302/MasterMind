package com.example.mastermind.model.game;

import android.net.Uri;

public class Record {
    private long time;
    private String id;

    public Record() {
    }

    public Record(long time, String id) {
        this.time = time;
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
