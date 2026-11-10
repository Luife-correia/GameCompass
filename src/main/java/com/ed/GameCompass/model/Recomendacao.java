package com.ed.GameCompass.model;

public class Recomendacao {
    private Jogo jogo;
    private int pontuacao;

    public Recomendacao(Jogo jogo, int pontuacao) {
        this.jogo = jogo;
        this.pontuacao = pontuacao;
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

}
