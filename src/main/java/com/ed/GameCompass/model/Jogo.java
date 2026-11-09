package com.ed.GameCompass.model;

public class Jogo {

    private String nome;
    private String capa;
    private String descricao;
    private String[] generos;
    private String desenvolvedor;

    public Jogo(String nome, String capa, String descricao, String[] generos, String desenvolvedor) {
        this.nome = nome;
        this.capa = capa;
        this.descricao = descricao;
        this.generos = generos;
        this.desenvolvedor = desenvolvedor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCapa() {
        return capa;
    }

    public void setCapa(String capa) {
        this.capa = capa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String[] getGeneros() {
        return generos;
    }

    public void setGeneros(String[] generos) {
        this.generos = generos;
    }

    public String getDesenvolvedor() {
        return desenvolvedor;
    }

    public void setDesenvolvedor(String desenvolvedor) {
        this.desenvolvedor = desenvolvedor;
    }
}
