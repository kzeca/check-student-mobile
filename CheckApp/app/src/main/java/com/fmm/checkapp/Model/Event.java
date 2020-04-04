package com.fmm.checkapp.Model;

import androidx.annotation.NonNull;

import com.fmm.checkapp.firebasemodel.Events;
import com.fmm.checkapp.firebasemodel.Keys;
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
    private String url;
    private boolean isCheckInDone = false;
    private boolean isCheckOutDone = false;
    private String uIdTeacher;
    private List<Keys> keys;
    private String classEvent;
    private String uid;

    public Event(Events events, String uid, String uIdTeacher, String checkin, String checkout, List<Keys> keys) {
        this.startTime = events.getBegin();
        this.date = events.getDate();
        this.endTime = events.getEnd();
        this.title = events.getTitle();
        this.subject = events.getSubject();
        this.checkInTime = checkin;
        this.checkOutTime = checkout;
        this.url = events.getLink();
        this.uid = uid;
        this.uIdTeacher = uIdTeacher;
        this.keys = keys;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getuIdTeacher() {
        return uIdTeacher;
    }

    public void setuIdTeacher(String uIdTeacher) {
        this.uIdTeacher = uIdTeacher;
    }

    public List<Keys> getKeys() {
        return keys;
    }

    public void setKeys(List<Keys> keys) {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getTitle() + "," + this.getUrl() + "," + this.getStartTime() + this.getEndTime();
    }
}
