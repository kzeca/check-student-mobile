package com.fmm.checkapp.firebasemodel;

import java.util.HashMap;
import java.util.List;

public class Professores {

    String email;
    String name;
    String subject;
    HashMap<String,HashMap<String,Events>> events;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public HashMap<String, HashMap<String, Events>> getEvents() {
        return events;
    }

    public void setEvents(HashMap<String, HashMap<String, Events>> events) {
        this.events = events;
    }
}
