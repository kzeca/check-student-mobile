package com.fmm.checkapp.Model;

public class Event {
    private String subject;
    private String title;
    private String startTime;
    private String endTime;
    private String checkInTime;
    private String checkOutTime;
    private boolean isCheckInDone;
    private boolean isCheckOutDone;



    public Event() {
    }

    public Event(String subject, String title, String startTime, String endTime, String checkInTime, String checkOutTime) {
        this.subject = subject;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
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


}
