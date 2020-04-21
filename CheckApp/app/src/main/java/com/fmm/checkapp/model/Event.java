package com.fmm.checkapp.model;

import androidx.annotation.NonNull;

import com.fmm.checkapp.firebasemodel.Events;
import com.fmm.checkapp.firebasemodel.Keys;
import com.fmm.checkapp.task.TimeAsyncTask;
import com.fmm.checkapp.util.NetworkUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.fmm.checkapp.LoginActivity.user;

public class Event implements Comparable<Event>{
    private String subject;
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String checkInTime;
    private String checkOutTime;
    private String url;
    private boolean isCheckInDone ;
    private boolean isCheckOutDone;
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
        this.isCheckInDone=!checkin.isEmpty();
        this.isCheckOutDone=!checkout.isEmpty();
        this.uIdTeacher = uIdTeacher;
        this.keys=new ArrayList<Keys>();
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

    @Override
    public int compareTo(Event other) {


        String[] timeThis = this.startTime.split("h");
        String[] timeOther = other.getStartTime().split("h");
        int startTimeThis = (Integer.parseInt(timeThis[0])*60) + Integer.parseInt(timeThis[1]);
        int startTimeOther = (Integer.parseInt(timeOther[0])*60) + Integer.parseInt(timeOther[1]);

        if (startTimeThis>startTimeOther) return -1;
        else if(startTimeThis<startTimeOther) return 1;

        return 0;
    }
}
