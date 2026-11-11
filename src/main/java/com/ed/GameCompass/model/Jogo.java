package com.ed.GameCompass.model;

import java.time.LocalDate;

public class Jogo {

    private String nome;
    private String capa;
    private String descricao;
    private LocalDate dataLancamento;
    private String[] generos;
    private String desenvolvedor;

    public Jogo(String nome, String capa, String descricao, String[] generos, String desenvolvedor) {
        this(nome, capa, descricao, null, generos, desenvolvedor);
    }

    public Jogo(String nome, String capa, String descricao, LocalDate dataLancamento,
                String[] generos, String desenvolvedor) {
        this.nome = nome;
        this.capa = capa;
        this.descricao = descricao;
        this.dataLancamento = dataLancamento;
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

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
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
