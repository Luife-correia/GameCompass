package com.ed.GameCompass.grafo;

import java.util.ArrayList;

//Representa um elemento do grafo e suas conexões de entrada e saída.
public class Vertice<TIPO> {
    private TIPO dado;
    private ArrayList<Aresta<TIPO>> arestasEntrada;
    private ArrayList<Aresta<TIPO>> arestasSaida;

    //Cria um vértice e inicia suas listas de arestas.
    public Vertice(TIPO valor){
        this.dado = valor;
        this.arestasEntrada = new ArrayList<Aresta<TIPO>>();
        this.arestasSaida = new ArrayList<Aresta<TIPO>>();
    }


    //As listas são mantidas separadas para permitir navegação em cada direção.
    public TIPO getDado() {
        return dado;
    }

    public void setDado(TIPO dado) {
        this.dado = dado;
    }

    public ArrayList<Aresta<TIPO>> getArestasEntrada() {
        return arestasEntrada;
    }

    public void setArestasEntrada(ArrayList<Aresta<TIPO>> arestasEntrada) {
        this.arestasEntrada = arestasEntrada;
    }

    public ArrayList<Aresta<TIPO>> getArestasSaida() {
        return arestasSaida;
    }

    public void setArestasSaida(ArrayList<Aresta<TIPO>> arestasSaida) {
        this.arestasSaida = arestasSaida;
    }

    //Registra uma aresta que chega a este vértice.
    public void adicionarArestaEntrada (Aresta<TIPO> aresta){
        this.arestasEntrada.add(aresta);
    }

    //Registra uma aresta que parte deste vértice.
    public void adicionarArestaSaida (Aresta<TIPO> aresta){
        this.arestasSaida.add(aresta);
    }

}
