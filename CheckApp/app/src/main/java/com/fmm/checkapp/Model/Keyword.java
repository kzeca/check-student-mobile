package com.fmm.checkapp.Model;

public class Keyword {
    private String key;
    private String time;

    public Keyword(){
        
    }
    public Keyword(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
