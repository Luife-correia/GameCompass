package com.ed.GameCompass.model;

 //Representa um jogo recomendado e sua pontuação de similaridade.
 //O índice identifica a posição do jogo no catálogo e é usado para montar o
 //link da página de recomendações correspondente.
public class Recomendacao {
    private Jogo jogo;
    private int pontuacao;
    private int indice;

    //Cria uma recomendação com o jogo, a pontuação e seu índice no catálogo.
    public Recomendacao(Jogo jogo, int pontuacao, int indice) {
        this.jogo = jogo;
        this.pontuacao = pontuacao;
        this.indice = indice;
    }

    // Propriedades expostas ao template Thymeleaf para renderização da página.
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
