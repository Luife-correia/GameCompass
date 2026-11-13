package com.ed.GameCompass.model;

import java.time.LocalDate;

//Representa um jogo exibido no catálogo.
//Além dos dados descritivos, a classe armazena os gêneros usados no cálculo
//das recomendações e a data opcional de lançamento.
public class Jogo {

    private String nome;
    private String capa;
    private String descricao;
    private LocalDate dataLancamento;
    private String[] generos;
    private String desenvolvedor;

    //Cria um jogo sem data de lançamento informada.
    public Jogo(String nome, String capa, String descricao, String[] generos, String desenvolvedor) {
        this(nome, capa, descricao, null, generos, desenvolvedor);
    }

    //Cria um jogo com todos os dados apresentados no catálogo.
    public Jogo(String nome, String capa, String descricao, LocalDate dataLancamento,
                String[] generos, String desenvolvedor) {
        this.nome = nome;
        this.capa = capa;
        this.descricao = descricao;
        this.dataLancamento = dataLancamento;
        this.generos = generos;
        this.desenvolvedor = desenvolvedor;
    }

    // Getters e setters permitem que o Thymeleaf leia e, quando necessário,
    // atualize os dados do modelo por meio das propriedades JavaBean.
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
