package com.ed.GameCompass.service;

import com.ed.GameCompass.model.Jogo;
import com.ed.GameCompass.model.Recomendacao;
import com.ed.GameCompass.grafo.Aresta;
import com.ed.GameCompass.grafo.Grafo;
import com.ed.GameCompass.grafo.Vertice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
//Centraliza o catálogo e a lógica de recomendação do GameCompass.
//Cada jogo é representado por um vértice, e cada relação de similaridade
//entre dois jogos é representada por uma aresta ponderada.
public class RecomendacaoService {

    // Catálogo imutável usado tanto pela tela quanto pelo grafo de similaridade.
    private final List<Jogo> catalogo = List.of(
            new Jogo("The Witcher 3", "https://cdn.cloudflare.steamstatic.com/steam/apps/292030/library_600x900.jpg", "The Witcher 3: Wild Hunt — Remastered moderniza o aclamado jogo de RPG com uma série de novidades e melhorias. Torne-se o caçador de monstros de aluguel Geralt de Rívia e viaje por um vasto mundo aberto de fantasia sombria em uma missão épica para encontrar sua filha adotiva, Ciri.", LocalDate.of(2015, 5, 18), new String[]{"RPG", "Acao", "Aventura", "Fantasia Sombria", "Mundo Aberto", "Narrativa", "Escolhas"}, "CD Projekt Red"),
            new Jogo("Cyberpunk 2077", "https://cdn.cloudflare.steamstatic.com/steam/apps/1091500/library_600x900.jpg", "Cyberpunk 2077 é um RPG de ação e aventura em mundo aberto que se passa em Night City, uma megalópole perigosa onde todos são obcecados por poder, glamour e alterações corporais.", LocalDate.of(2020, 12, 10), new String[]{"RPG", "Acao", "Mundo Aberto", "Cyberpunk", "Tiro", "Narrativa", "Escolhas"}, "CD Projekt Red"),
            new Jogo("Elden Ring", "https://cdn.cloudflare.steamstatic.com/steam/apps/1245620/library_600x900.jpg", "O NOVO RPG DE AÇÃO E FANTASIA. Levante-se, Maculado, e seja guiado pela graça para portar o poder do Anel Prístino e se tornar um Lorde Prístino nas Terras Intermédias.", LocalDate.of(2022, 2, 25), new String[]{"RPG", "Acao", "Fantasia", "Soulslike", "Mundo Aberto", "Combate Difícil", "Exploracao"}, "FromSoftware"),
            new Jogo("Dark Souls III", "https://cdn.cloudflare.steamstatic.com/steam/apps/374320/library_600x900.jpg", "DARK SOULS™ continua a ultrapassar seus próprios limites em um ambicioso novo capítulo da série que definiu um gênero e que é aclamada pela crítica. Prepare-se para abraçar a escuridão!", LocalDate.of(2016, 4, 12), new String[]{"RPG", "Acao", "Fantasia Sombria", "Soulslike", "Combate Difícil", "Exploracao", "Chefes"}, "FromSoftware"),
            new Jogo("Sekiro: Shadows Die Twice", "https://cdn.cloudflare.steamstatic.com/steam/apps/814380/library_600x900.jpg", "Jogo do Ano - The Game Awards 2019 Melhor Jogo de Ação de 2019 - IGN Trilhe seu próprio caminho de vingança nesta premiada aventura da FromSoftware, os criadores de Bloodborne e da franquia Dark Souls. Obtenha vingança. Retome sua honra. Mate astuciosamente.", LocalDate.of(2019, 3, 22), new String[]{"Acao", "Aventura", "Fantasia", "Soulslike", "Ninja", "Combate Difícil", "Parry"}, "FromSoftware"),
            new Jogo("God of War", "https://cdn.cloudflare.steamstatic.com/steam/apps/1593500/library_600x900.jpg", "Com a vingança contra os deuses do Olimpo em um passado distante, Kratos agora vive como um mortal no reino dos deuses e monstros nórdicos. É nesse mundo duro e implacável que ele deve lutar para sobreviver... e ensinar seu filho a fazer o mesmo.", LocalDate.of(2022, 1, 14), new String[]{"Acao", "Aventura", "Fantasia", "Mitologia", "Narrativa", "Combate", "Exploracao"}, "Santa Monica Studio"),
            new Jogo("Horizon Zero Dawn", "https://cdn.cloudflare.steamstatic.com/steam/apps/1151640/library_600x900.jpg", "Viva a experiência de Aloy em sua busca lendária para desvendar os mistérios de uma Terra do futuro controlada por máquinas. Use ataques táticos devastadores contra suas presas e explore um mundo aberto majestoso neste premiado RPG de aventura e ação!", LocalDate.of(2020, 8, 7), new String[]{"Acao", "Aventura", "Mundo Aberto", "RPG", "Ficcao Cientifica", "Caca", "Exploracao"}, "Guerrilla Games"),
            new Jogo("The Last of Us Part I", "https://cdn.cloudflare.steamstatic.com/steam/apps/1888930/library_600x900.jpg", "Descubra o jogo premiado que inspirou a série de TV aclamada pela crítica. Desbrave a América pós-apocalíptica com Joel e Ellie e conheça aliados e inimigos inesquecíveis em The Last of Us™.", LocalDate.of(2023, 3, 28), new String[]{"Acao", "Aventura", "Drama", "Terror", "Sobrevivencia", "Narrativa", "Pos-Apocaliptico"}, "Naughty Dog"),
            new Jogo("UNCHARTED™: Coleção Legado dos Ladrões", "https://cdn.cloudflare.steamstatic.com/steam/apps/1659420/library_600x900.jpg", "UNCHARTED: Coleção Legado dos Ladrões oferece a forma definitiva de jogar duas das aventuras mais emocionantes da premiada desenvolvedora Naughty Dog, remasterizadas com melhorias específicas para PC", LocalDate.of(2022, 10, 19), new String[]{"Acao", "Aventura", "Exploracao", "Tiro", "Plataforma", "Narrativa", "Tesouros"}, "Naughty Dog"),
            new Jogo("Red Dead Redemption 2", "https://cdn.cloudflare.steamstatic.com/steam/apps/1174180/library_600x900.jpg", "Arthur Morgan e a gangue Van der Linde são forçados a fugir. Com agentes federais e caçadores de recompensas no seu encalço, a gangue precisa roubar, assaltar e lutar para sobreviver no impiedoso coração dos Estados Unidos.", LocalDate.of(2019, 12, 5), new String[]{"Acao", "Aventura", "Mundo Aberto", "Velho Oeste", "Narrativa", "Crime", "Simulacao"}, "Rockstar Games"),
            new Jogo("Grand Theft Auto V", "https://cdn.cloudflare.steamstatic.com/steam/apps/271590/library_600x900.jpg", "Aproveite os fenômenos do entretenimento Grand Theft Auto V e Grand Theft Auto Online melhorados para uma nova geração, com gráficos deslumbrantes, tempos de carregamento mais rápidos, áudio 3D e mais, além de conteúdo exclusivo para jogadores do GTA Online.", LocalDate.of(2015, 4, 14), new String[]{"Acao", "Mundo Aberto", "Crime", "Tiro", "Veiculos", "Multijogador", "Sandbox"}, "Rockstar Games"),
            new Jogo("Assassin's Creed Odyssey", "https://cdn.cloudflare.steamstatic.com/steam/apps/812140/library_600x900.jpg", "Embarque rumo à Grécia Antiga neste jogo de ação e aventura para alterar o destino da civilização. Trave batalhas intensas em terra e mar, molde seu destino de exilado a lenda e desvende os segredos do seu passado.", LocalDate.of(2018, 10, 5), new String[]{"RPG", "Acao", "Mundo Aberto", "Historia", "Mitologia", "Exploracao", "Escolhas"}, "Ubisoft"),
            new Jogo("Far Cry 6", "https://cdn.cloudflare.steamstatic.com/steam/apps/2369390/library_600x900.jpg", "Lidere a revolução neste jogo de tiro em primeira pessoa de guerrilha em mundo aberto. Lute com armas improvisadas, viaje como puder e construa a sua rede de rebeldes para derrubar o regime de Castillo.", LocalDate.of(2022, 5, 11), new String[]{"Acao", "Tiro", "Mundo Aberto", "Furtividade", "Guerrilha", "Crafting", "Veiculos"}, "Ubisoft"),
            new Jogo("Resident Evil 4", "https://cdn.cloudflare.steamstatic.com/steam/apps/2050650/library_600x900.jpg", "Sobrevivência é apenas o começo. Seis anos se passaram desde o desastre biológico em Raccoon City. Leon S. Kennedy, um dos sobreviventes, segue o rastro da raptada filha do presidente até uma vila europeia isolada, onde há algo terrivelmente errado com os habitantes.", LocalDate.of(2023, 3, 24), new String[]{"Terror", "Acao", "Sobrevivencia", "Horror", "Zumbis", "Tiro", "Quebra-Cabecas"}, "Capcom"),
            new Jogo("Monster Hunter: World", "https://cdn.cloudflare.steamstatic.com/steam/apps/582010/library_600x900.jpg", "Conheça o Novo Mundo! Em Monster Hunter: World, o jogo mais recente da série, você pode curtir a experiência máxima de caçada, usando tudo à sua disposição para caçar monstros em um novo mundo repleto de surpresas e emoções.", LocalDate.of(2018, 8, 9), new String[]{"RPG", "Acao", "Sobrevivencia", "Caca", "Crafting", "Cooperativo", "Chefes"}, "Capcom"),
            new Jogo("Minecraft", "https://images.igdb.com/igdb/image/upload/t_cover_big_2x/co8fu6.jpg", "Crie e molde um mundo infinito, explore diferentes biomas cheios de criaturas e surpresas, embarque em aventuras emocionantes em lugares perigosos e enfrente inimigos misteriosos", LocalDate.of(2011, 11, 18), new String[]{"Sobrevivencia", "Aventura", "Mundo Aberto", "Sandbox", "Construcao", "Crafting", "Exploracao", "Cooperativo"}, "Mojang Studios"),
            new Jogo("Stardew Valley", "https://cdn.cloudflare.steamstatic.com/steam/apps/413150/library_600x900.jpg", "Você herdou a antiga fazenda do seu avô, em Stardew Valley. Com ferramentas de segunda-mão e algumas moedas, você parte para dar início a sua nova vida. Será que você vai aprender a viver da terra, a transformar esse matagal em um próspero lar?", LocalDate.of(2016, 2, 26), new String[]{"Simulacao", "RPG", "Aventura", "Sandbox", "Construcao", "Crafting", "Agricultura", "Vida", "Cooperativo"}, "ConcernedApe"),
            new Jogo("Hades", "https://cdn.cloudflare.steamstatic.com/steam/apps/1145360/library_600x900.jpg", "Desafie o deus dos mortos enquanto você batalha para sair do Submundo neste jogo roguelike dos mesmos criadores de Bastion, Transistor e Pyre.", LocalDate.of(2020, 9, 17), new String[]{"RPG", "Acao", "Roguelike"}, "Supergiant Games"),
            new Jogo("Hollow Knight", "https://cdn.cloudflare.steamstatic.com/steam/apps/367520/library_600x900.jpg", "Forje seu caminho em Hollow Knight! Uma aventura de ação épica em um vasto reino arruinado de insetos e heróis. Explore cavernas serpenteantes, lute contra criaturas malignas e alie-se a insetos bizarros num estilo clássico 2D desenhado à mão.", LocalDate.of(2017, 2, 24), new String[]{"Acao", "Aventura", "Metroidvania", "Indie"}, "Team Cherry"),
            new Jogo("Dead Cells", "https://cdn.cloudflare.steamstatic.com/steam/apps/588650/library_600x900.jpg", "Dead Cells é um \"roguelite\" de ação em plataforma estilo Metroidvania. Você vai explorar um castelo extenso e em constante mutação... Sem checkpoints. Mate, morra, aprenda, repita.", LocalDate.of(2018, 8, 7), new String[]{"Acao", "Aventura", "Roguelike"}, "Motion Twin"),
            new Jogo("Terraria", "https://cdn.cloudflare.steamstatic.com/steam/apps/105600/library_600x900.jpg", "Cave, lute, explore, construa! Nada é impossível neste jogo de aventura cheio de ação.", LocalDate.of(2011, 5, 16), new String[]{"Acao", "Aventura", "Sobrevivencia", "Sandbox", "Construcao", "Crafting", "Exploracao", "2D", "Cooperativo"}, "Re-Logic"),
            new Jogo("Dead Space", "https://cdn.cloudflare.steamstatic.com/steam/apps/1693980/library_600x900.jpg", "O clássico de terror de sobrevivência e ficção científica está de volta, totalmente reformulado para oferecer uma experiência ainda mais imersiva, incluindo aprimoramentos visuais, de áudio e de jogabilidade, ao mesmo tempo em que se mantém fiel à emocionante visão do jogo original.", LocalDate.of(2023, 1, 27), new String[]{"Terror", "Acao", "Sobrevivencia", "Horror", "Tiro"}, "Motive Studio"),
            new Jogo("Batman: Arkham City", "https://cdn.cloudflare.steamstatic.com/steam/apps/200260/library_600x900.jpg", "Batman: Arkham City expande a história atmosférica e intensa de Batman: Arkham Asylum, colocando jogadores na expansiva Arkham City, cinco vezes maior que o espaço jogável em Batman: Arkham Asylum – o novo \"lar\" de segurança máxima para todos os bandidos, gângsteres e mentes criminosas insanas de Gotham City. Com uma incrível coletânea dos criminosos mais perigosos de Gotham City, incluindo Mulher-Gato, Coringa, Charada, Duas-Caras, Arlequina, Pinguim, Senhor Frio e muitos outros, os jogadores podem sentir a experiência de ser o Cavaleiro das Trevas enquanto defendem a justiça nas ruas de Gotham City.", LocalDate.of(2011, 11, 22), new String[]{"Acao", "Aventura", "Mundo Aberto"}, "Rocksteady Studios"),
            new Jogo("Celeste", "https://cdn.cloudflare.steamstatic.com/steam/apps/504230/library_600x900.jpg", "Ajude Madeline a enfrentar seus demônios internos em sua jornada até o topo da Montanha Celeste, nesse jogo de plataforma super afiado dos criadores de TowerFall. Desbrave centenas de desafios meticulosos, descubra segredos complicados e desvende o mistério da montanha.\n", LocalDate.of(2018, 1, 25), new String[]{"Plataforma", "Aventura", "Indie"}, "Maddy Makes Games")
    );

    // Grafo construído uma vez durante a criação do service.
    private final Grafo<Jogo> grafo = new Grafo<>();

    //Monta o grafo de recomendações assim que o service é criado.
    public RecomendacaoService() {
        construirGrafo();
    }

    //Retorna os jogos disponíveis para consulta.
    public List<Jogo> getCatalogo() {
        return catalogo;
    }

    //Cria os vértices e as arestas ponderadas do grafo.
    private void construirGrafo() {
        //Cada jogo vira um vértice do grafo.
        for (Jogo jogo : catalogo) {
            grafo.adicionarVertice(jogo);
        }

        //A relação é criada nos dois sentidos para o grafo ser não direcionado.
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

     //Calcula a pontuação de similaridade entre dois jogos.
     //Cada gênero compartilhado soma um ponto; o mesmo desenvolvedor soma
     //três pontos adicionais, valorizando relações de catálogo e estúdio.
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

    //Obtém os vizinhos do jogo base e os ordena da maior para a menor pontuação.
    public List<Recomendacao> ordenarPorSimilaridade(Jogo jogoBase){

        ArrayList<Recomendacao> recomendacoes = new ArrayList<>();
        Vertice<Jogo> verticeBase = grafo.getVertice(jogoBase);

        // As recomendações agora vêm das arestas que saem do jogo base.
        for (Aresta<Jogo> aresta : verticeBase.getArestasSaida()) {
            Jogo jogoRecomendado = aresta.getFim().getDado();
            int indiceRecomendado = catalogo.indexOf(jogoRecomendado);
            recomendacoes.add(new Recomendacao(jogoRecomendado, aresta.getPeso().intValue(), indiceRecomendado));
        }

        recomendacoes.sort(
                Comparator.comparingInt(Recomendacao::getPontuacao).reversed());

        return recomendacoes;

    }
}
