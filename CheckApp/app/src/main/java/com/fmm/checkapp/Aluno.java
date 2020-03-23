package com.fmm.checkapp;

public class Aluno extends Usuario {

    private String checkInHour;
    private String checkOutHour;
    private String ra;
    private String keyWord;

    public Aluno(){

    }
    public Aluno(String checkInHour, String checkOutHour, String ra, String keyWord) {
        this.checkInHour = checkInHour;
        this.checkOutHour = checkOutHour;
        this.ra = ra;
        this.keyWord = keyWord;
    }

    public String getCheckInHour() {
        return checkInHour;
    }

    public void setCheckInHour(String checkInHour) {
        this.checkInHour = checkInHour;
    }

    public String getCheckOutHour() {
        return checkOutHour;
    }

    public void setCheckOutHour(String checkOutHour) {
        this.checkOutHour = checkOutHour;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
