package com.fmm.checkapp.Model;

import androidx.annotation.NonNull;

import com.fmm.checkapp.Usuario;

import java.util.List;

public class Professor extends Usuario {

    private List<Event> eventos;


    public Professor(){}
    public Professor( String name, String email ,String uid, List<Event> eventos) {
        this.eventos = eventos;
        super.setNome(name);
        super.setEmail(email);
        super.setuId(uid);

    }



    public List<Event> getEventos() {
        return eventos;
    }

    public void setEventos(List<Event> eventos) {
        this.eventos = eventos;
    }


}
