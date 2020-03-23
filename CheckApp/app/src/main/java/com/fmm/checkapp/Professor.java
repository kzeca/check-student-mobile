package com.fmm.checkapp;

import java.util.List;

public class Professor extends Usuario {

    private String materia;
    private List<EventAula> eventos;


    public Professor(){

    }
    public Professor(String materia, List<EventAula> eventos) {
        this.materia = materia;
        this.eventos = eventos;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public List<EventAula> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventAula> eventos) {
        this.eventos = eventos;
    }
}
