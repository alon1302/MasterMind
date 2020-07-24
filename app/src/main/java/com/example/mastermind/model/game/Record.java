package com.example.mastermind.model.game;

public class Record {
    private long time;
    private String userId;
    private String id;

    public Record() {
    }

    public Record(long time, String userId, String id) {
        this.time = time;
        this.userId = userId;
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
