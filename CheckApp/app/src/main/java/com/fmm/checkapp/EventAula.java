package com.fmm.checkapp;

import java.util.Date;
import java.util.List;

class EventAula {
    private String sala;
    private String beginHour;
    private String endHour;
    private Date date;
    private String description;
    private String title;
    private List<KeyAula> keyWords;


    public EventAula(String sala, String beginHour, String endHour, Date date, String description, String title, List<KeyAula> keyWords) {
        this.sala = sala;
        this.beginHour = beginHour;
        this.endHour = endHour;
        this.date = date;
        this.description = description;
        this.title = title;
        this.keyWords = keyWords;
    }

    public EventAula(){

    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public String getBeginHour() {
        return beginHour;
    }

    public void setBeginHour(String beginHour) {
        this.beginHour = beginHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<KeyAula> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(List<KeyAula> keyWords) {
        this.keyWords = keyWords;
    }
}
