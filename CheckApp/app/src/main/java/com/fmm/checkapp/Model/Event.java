package com.fmm.checkapp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.ArrayList;

public class Event {
    private String subject;
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String checkInTime;
    private String checkOutTime;
    private String url;
    private boolean isCheckInDone;
    private boolean isCheckOutDone;
    private String uIdTeacher;
    private List<Keyword> keys;
    private String classEvent;
    private String eventId;

    public Event(DataSnapshot dados, String uid, String classEvent) {
        this.startTime = dados.child("begin").getValue().toString();
        this.date = dados.child("date").getValue().toString();
        this.endTime = dados.child("end").getValue().toString();
        this.keys = new ArrayList<>();
        for (DataSnapshot dadosAux : dados.child("keys").getChildren()) {
            Keyword key = dadosAux.getValue(Keyword.class);
            this.keys.add(key);
        }

        this.title = dados.child("title").getValue().toString();
        this.subject = dados.child("subject").getValue().toString();
        this.checkInTime = "";
        this.checkOutTime = "";
        this.uIdTeacher = uid;
        this.classEvent = classEvent;
        this.url = dados.child("link").getValue().toString();
        this.eventId = dados.getKey();
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

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("professores")
                .child(uIdTeacher)
                .child("events")
                .child(classEvent)
                .child(eventId)
                .child("students")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("checkin");
        reference.setValue(checkInTime);


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getuIdTeacher() + "," +this.eventId;
    }
}
