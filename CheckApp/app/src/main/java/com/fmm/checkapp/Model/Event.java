package com.fmm.checkapp.Model;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class Event {
    private String subject;
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String checkInTime;
    private String checkOutTime;
    private boolean isCheckInDone;
    private boolean isCheckOutDone;
    private String uIdTeacher;
    private List<Keyword> keys;
    private String classEvent;



    public Event(DataSnapshot dados, String uid, String classEvent) {
        this.startTime=dados.child("begin").getValue().toString();
        this.date=dados.child("date").getValue().toString();
        this.endTime=dados.child("end").getValue().toString();
        for(DataSnapshot keys_dados:dados.child("keys").getChildren()){
            this.keys.add((Keyword) keys_dados.getValue());
        }
        this.title =dados.child("title").getValue().toString();
        this.subject = dados.child("subject").getValue().toString();
        this.checkInTime = "";
        this.checkOutTime = "";
        this.uIdTeacher = uid;
        this.classEvent = classEvent;

    }

    public Event(String subject, String title, String startTime, String endTime, String checkInTime, String checkOutTime) {
        this.subject = subject;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public Event(String subject, String title, String startTime, String endTime, String checkInTime, String checkOutTime, String uIdTeacher) {
        this.subject = subject;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.uIdTeacher = uIdTeacher;
    }

    public Event(String subject, String title, String startTime, String endTime, String checkInTime, String checkOutTime, String uIdTeacher, List<Keyword> keys) {
        this.subject = subject;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.uIdTeacher = uIdTeacher;
        this.keys = keys;
    }

    public Event(String subject, String title, String startTime, String endTime, String checkInTime, String checkOutTime, String uIdTeacher, List<Keyword> keys, String classEvent) {
        this.subject = subject;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.uIdTeacher = uIdTeacher;
        this.keys = keys;
        this.classEvent = classEvent;
    }

    public boolean isCheckOutDone() {
        return isCheckOutDone;
    }

    public void setCheckOutDone(boolean checkOutDone) {
        isCheckOutDone = checkOutDone;
    }

    public boolean isCheckInDone() {
        return isCheckInDone;
    }

    public void setCheckInDone(boolean checkInDone) {
        isCheckInDone = checkInDone;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getuIdTeacher() {  return uIdTeacher; }

    public void setuIdTeacher(String uIdTeacher) { this.uIdTeacher = uIdTeacher; }

    public List<Keyword> getKeys() {
        return keys;
    }

    public void setKeys(List<Keyword> keys) {
        this.keys = keys;
    }

    public String getClassEvent() {
        return classEvent;
    }

    public void setClassEvent(String classEvent) {
        this.classEvent = classEvent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
