package com.fmm.checkapp;

import android.widget.Adapter;

public class Usuario {

    private String nome;
    private String email;
    private String senha;
    private String ra;
    private String uId;
    private String turma;
    private boolean professor;

    public Usuario(){

    }
    public Usuario(String nome, String email, String senha, String uId, String turma, boolean professor) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.uId = uId;
        this.turma = turma;
        this.professor = professor;
    }

    public boolean isProfessor() {
        return professor;
    }

    public void setProfessor(boolean professor) {
        this.professor = professor;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

}
