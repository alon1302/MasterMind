package com.example.mastermind.model.game;

public class Record {
    private long time;
    private String name;
    private String imgUrl;

    public Record() {
    }

    public Record(long time, String name, String imgUrl) {
        this.time = time;
        this.name = name;
        this.imgUrl = imgUrl;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Record{" +
                "time=" + time +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
