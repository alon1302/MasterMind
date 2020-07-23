package com.example.mastermind.model.firebase;

import androidx.lifecycle.MutableLiveData;

import com.example.mastermind.model.user.User;

public class RecordPerUser {

    private long time;
    private MutableLiveData<User> user;

    public RecordPerUser(long time, MutableLiveData<User> user) {
        this.time = time;
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(MutableLiveData<User> user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "RecordPerUser{" +
                "time=" + time +
                ", user=" + user.getValue() +
                '}';
    }
}
