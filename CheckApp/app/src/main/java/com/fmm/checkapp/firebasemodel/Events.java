package com.fmm.checkapp.firebasemodel;

import java.util.HashMap;

public class Events {

    String begin;
    String date;
    String description;
    String end;
    String link;
    String subject;
    String title;
    HashMap<String, Keys> keys;
    HashMap<String, Students> students;

    public HashMap<String, Students> getStudents() {
        return students;
    }

    public void setStudents(HashMap<String, Students> students) {
        this.students = students;
    }

    public HashMap<String, Keys> getKeys() {
        return keys;
    }

    public void setKeys(HashMap<String, Keys> keys) {
        this.keys = keys;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
