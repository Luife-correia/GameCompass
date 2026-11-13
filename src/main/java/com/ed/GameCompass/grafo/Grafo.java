package com.ed.GameCompass.grafo;

import java.util.ArrayList;

//Estrutura genérica que armazena vértices e arestas de um grafo.
//O tipo genérico permite usar a mesma estrutura para jogos ou outros dados.
public class Grafo<TIPO> {
    private ArrayList<Vertice<TIPO>> vertices;
    private ArrayList<Aresta<TIPO>> arestas;

    //Inicializa as coleções que armazenam os elementos do grafo.
    public Grafo(){
        this.vertices = new ArrayList<Vertice<TIPO>>();
        this.arestas = new ArrayList<Aresta<TIPO>>();
    }

    //Adiciona um novo vértice contendo o dado informado.
    public void adicionarVertice(TIPO dado){
        Vertice<TIPO> novoVertice = new Vertice<TIPO>(dado);
        this.vertices.add(novoVertice);
    }

    //Liga dois vértices com uma aresta direcionada e ponderada.
    public void adicionarAresta (Double peso, TIPO dadoInicio, TIPO dadoFim) {
        Vertice<TIPO> inicio = this.getVertice(dadoInicio);
        Vertice<TIPO> fim = this.getVertice(dadoFim);
        Aresta<TIPO> aresta = new Aresta<TIPO>(peso, inicio, fim);
        inicio.adicionarArestaSaida(aresta);
        fim.adicionarArestaEntrada(aresta);
        this.arestas.add(aresta);
    }

    //Localiza o vértice cujo dado é igual ao valor informado.
    public Vertice<TIPO> getVertice (TIPO dado){
        Vertice<TIPO> vertice = null;
        for(int i=0; i < this.vertices.size(); i++) {
            if (this.vertices.get(i).getDado().equals(dado)) {
                vertice = this.vertices.get(i);
                break;
            }
        }
        return vertice;
    }

}
