package com.ed.GameCompass.model;

public class Recomendacao {
    private Jogo jogo;
    private int pontuacao;
    private int indice;

    public Recomendacao(Jogo jogo, int pontuacao, int indice) {
        this.jogo = jogo;
        this.pontuacao = pontuacao;
        this.indice = indice;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

}
