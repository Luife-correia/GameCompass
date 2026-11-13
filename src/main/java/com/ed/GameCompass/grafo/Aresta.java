package com.ed.GameCompass.grafo;

/**
 * Representa uma conexão ponderada entre dois vértices.
 *
 * Inicio e fim permitem navegar pela relação, enquanto o peso representa a
 * intensidade da similaridade entre os jogos.
 */
public class Aresta<TIPO> {

    private Double peso;

    private Vertice<TIPO> inicio;

    private Vertice<TIPO> fim;

    //Cria uma aresta com peso, vértice de origem e vértice de destino.
    public Aresta (Double peso, Vertice<TIPO> inicio, Vertice<TIPO> fim) {
        this.peso = peso;
        this.inicio = inicio;
        this.fim = fim;
    }


    // Getters e setters mantêm os dados da relação acessíveis ao algoritmo.
    public Double getPeso () {
        return peso;
    }

    public void setPeso (Double peso) {
        this.peso = peso;
    }

    public Vertice<TIPO> getInicio () {
        return inicio;
    }

    public void setInicio (Vertice<TIPO> inicio) {
        this.inicio = inicio;
    }

    public Vertice<TIPO> getFim() {
        return fim;
    }

    public void setFim (Vertice<TIPO> fim) {
        this.fim = fim;
    }

}
