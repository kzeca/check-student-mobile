package com.fmm.checkapp.Model;

public class Keyword {
    private String key;
    private String time;

    public Keyword(){

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Keyword(String key, String time) {
        this.key = key;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
