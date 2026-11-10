package com.ed.GameCompass.service;

import com.ed.GameCompass.model.Jogo;
import com.ed.GameCompass.model.Recomendacao;
import com.ed.GameCompass.grafo.Aresta;
import com.ed.GameCompass.grafo.Grafo;
import com.ed.GameCompass.grafo.Vertice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecomendacaoService {

    private final List<Jogo> catalogo = List.of(
            new Jogo("The Witcher 3", "witcher-3.jpg", "RPG de mundo aberto com uma grande aventura.", new String[]{"RPG", "Acao", "Aventura"}, "CD Projekt Red"),
            new Jogo("Cyberpunk 2077", "cyberpunk-2077.jpg", "RPG futurista de mundo aberto.", new String[]{"RPG", "Acao", "Mundo Aberto"}, "CD Projekt Red"),
            new Jogo("Elden Ring", "elden-ring.jpg", "RPG de acao em um mundo fantastico.", new String[]{"RPG", "Acao", "Fantasia"}, "FromSoftware"),
            new Jogo("Dark Souls III", "dark-souls-3.jpg", "RPG desafiador em um universo sombrio.", new String[]{"RPG", "Acao", "Fantasia"}, "FromSoftware"),
            new Jogo("Sekiro: Shadows Die Twice", "sekiro.jpg", "Acao e aventura com combate baseado em precisao.", new String[]{"Acao", "Aventura", "Fantasia"}, "FromSoftware"),
            new Jogo("God of War", "god-of-war.jpg", "Aventura epica inspirada na mitologia nordica.", new String[]{"Acao", "Aventura", "Fantasia"}, "Santa Monica Studio"),
            new Jogo("Horizon Zero Dawn", "horizon-zero-dawn.jpg", "Aventura em um mundo aberto dominado por maquinas.", new String[]{"Acao", "Aventura", "Mundo Aberto"}, "Guerrilla Games"),
            new Jogo("The Last of Us Part I", "the-last-of-us.jpg", "Aventura narrativa em um mundo pos-apocaliptico.", new String[]{"Acao", "Aventura", "Drama"}, "Naughty Dog"),
            new Jogo("Uncharted 4", "uncharted-4.jpg", "Aventura cinematografica de exploracao e misterio.", new String[]{"Acao", "Aventura", "Exploracao"}, "Naughty Dog"),
            new Jogo("Red Dead Redemption 2", "red-dead-redemption-2.jpg", "Aventura em mundo aberto no velho oeste.", new String[]{"Acao", "Aventura", "Mundo Aberto"}, "Rockstar Games"),
            new Jogo("Grand Theft Auto V", "gta-5.jpg", "Acao em mundo aberto com diferentes protagonistas.", new String[]{"Acao", "Mundo Aberto", "Crime"}, "Rockstar Games"),
            new Jogo("Assassin's Creed Odyssey", "assassins-creed-odyssey.jpg", "RPG de acao ambientado na Grecia Antiga.", new String[]{"RPG", "Acao", "Mundo Aberto"}, "Ubisoft"),
            new Jogo("Far Cry 6", "far-cry-6.jpg", "Acao em primeira pessoa em uma ilha sob ditadura.", new String[]{"Acao", "Tiro", "Mundo Aberto"}, "Ubisoft"),
            new Jogo("Resident Evil 4", "resident-evil-4.jpg", "Terror e sobrevivencia contra uma ameaca biologica.", new String[]{"Terror", "Acao", "Sobrevivencia"}, "Capcom"),
            new Jogo("Monster Hunter: World", "monster-hunter-world.jpg", "RPG de acao focado na caca de monstros.", new String[]{"RPG", "Acao", "Sobrevivencia"}, "Capcom"),
            new Jogo("Minecraft", "minecraft.jpg", "Construcao e sobrevivencia em um mundo de blocos.", new String[]{"Sobrevivencia", "Aventura", "Mundo Aberto"}, "Mojang Studios"),
            new Jogo("Stardew Valley", "stardew-valley.jpg", "Simulacao de fazenda com exploracao e relacionamentos.", new String[]{"Simulacao", "RPG", "Aventura"}, "ConcernedApe"),
            new Jogo("Hades", "hades.jpg", "Roguelike de acao inspirado na mitologia grega.", new String[]{"RPG", "Acao", "Roguelike"}, "Supergiant Games"),
            new Jogo("Hollow Knight", "hollow-knight.jpg", "Aventura de acao em um reino subterraneo.", new String[]{"Acao", "Aventura", "Metroidvania"}, "Team Cherry"),
            new Jogo("Celeste", "celeste.jpg", "Plataforma e aventura sobre superar desafios.", new String[]{"Plataforma", "Aventura", "Indie"}, "Maddy Makes Games")
    );

    private final Grafo<Jogo> grafo = new Grafo<>();

    public RecomendacaoService() {
        construirGrafo();
    }

    public List<Jogo> getCatalogo() {
        return catalogo;
    }

    private void construirGrafo() {
        // Cada jogo vira um vértice do grafo.
        for (Jogo jogo : catalogo) {
            grafo.adicionarVertice(jogo);
        }

        // A relação é criada nos dois sentidos para o grafo ser não direcionado.
        for (int i = 0; i < catalogo.size(); i++) {
            for (int j = i + 1; j < catalogo.size(); j++) {
                Jogo jogoAtual = catalogo.get(i);
                Jogo outroJogo = catalogo.get(j);
                int similaridade = calcularSimilaridade(jogoAtual, outroJogo);

                if (similaridade > 0) {
                    grafo.adicionarAresta((double) similaridade, jogoAtual, outroJogo);
                    grafo.adicionarAresta((double) similaridade, outroJogo, jogoAtual);
                }
            }
        }
    }

    public int calcularSimilaridade(Jogo jogoBase, Jogo jogoCandidato) {
        int pontuacao = 0;

        for (int i = 0; i < jogoBase.getGeneros().length; i++) {
            for (int j = 0; j < jogoCandidato.getGeneros().length; j++) {
                if (jogoBase.getGeneros()[i].equals(jogoCandidato.getGeneros()[j])){
                    pontuacao++;
                    break;
                }
            }
        }

        if (jogoBase.getDesenvolvedor().equals(jogoCandidato.getDesenvolvedor())){
            pontuacao += 3;
        }

        return pontuacao;
    }

    public List<Recomendacao> ordenarPorSimilaridade(Jogo jogoBase){

        ArrayList<Recomendacao> recomendacoes = new ArrayList<>();
        Vertice<Jogo> verticeBase = grafo.getVertice(jogoBase);

        // As recomendações agora vêm das arestas que saem do jogo base.
        for (Aresta<Jogo> aresta : verticeBase.getArestasSaida()) {
            Jogo jogoRecomendado = aresta.getFim().getDado();
            recomendacoes.add(new Recomendacao(jogoRecomendado, aresta.getPeso().intValue()));
        }

        recomendacoes.sort(
                Comparator.comparingInt(Recomendacao::getPontuacao).reversed());

        return recomendacoes;

    }
}
