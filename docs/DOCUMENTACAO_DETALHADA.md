# Documentação Detalhada do GameCompass

Esta documentação explica o funcionamento das classes, templates e recursos principais do projeto **GameCompass**, bem como a comunicação entre o Front-End (HTML/Thymeleaf) e o Controller (Spring Boot).

---

## Sumário
1. [Visão Geral da Arquitetura](#visão-geral-da-arquitetura)
2. [Classe Principal: `GameCompassApplication.java`](#1-gamecompassapplicationjava)
3. [Camada de Controle: `JogoController.java`](#2-jogocontrollerjava)
4. [Estrutura de Dados (Grafos): `Aresta.java`, `Vertice.java`, `Grafo.java`](#3-estrutura-de-dados-grafos)
5. [Camada de Serviço: `RecomendacaoService.java`](#4-recomendacaoservicejava)
6. [Comunicação Front-End ⇄ Controller](#5-comunicação-front-end--controller)
7. [Modelos da aplicação](#6-modelos-da-aplicação)
8. [Templates e identidade visual](#7-templates-e-identidade-visual)
9. [Configuração, execução e testes](#8-configuração-execução-e-testes)

---

## Visão Geral da Arquitetura

O **GameCompass** é uma aplicação web desenvolvida em Java 17 com **Spring Boot 3.3.5** e **Thymeleaf**. O objetivo do sistema é recomendar jogos com base em similaridade de gênero e desenvolvedora, utilizando uma estrutura de dados de grafo.

O projeto segue o padrão arquitetural **MVC (Model-View-Controller)** dividido em camadas:
- **Model**: Dados da aplicação (`Jogo`, `Recomendacao`).
- **Controller**: Gerencia as rotas HTTP e interage com os serviços.
- **Service**: Contém a regra de negócio, o catálogo de jogos e a lógica do Grafo e de similaridade.
- **Grafo (`grafo`)**: Implementação customizada de Estrutura de Dados (Grafo, Vértice e Aresta).
- **View (`templates`)**: Páginas HTML renderizadas no servidor via Thymeleaf.

---

## 1. `GameCompassApplication.java`
**Localização:** `src/main/java/com/ed/GameCompass/GameCompassApplication.java`

Esta é a classe de entrada (bootstrap) da aplicação Spring Boot.

### Explicação Linha por Linha:
```java
package com.ed.GameCompass; // Define o pacote base da aplicação

import org.springframework.boot.SpringApplication; // Importa a classe para iniciar o Spring Boot
import org.springframework.boot.autoconfigure.SpringBootApplication; // Importa a anotação de configuração automática

@SpringBootApplication // Anotação combinada que habilita configuração automática, varredura de componentes (@Component, @Service, @Controller) e configuração de contexto
public class GameCompassApplication {

    public static void main(String[] args) {
        // Método principal padrão do Java. Inicia o contêiner do Spring Boot e levanta o servidor embarcado (Tomcat).
        SpringApplication.run(GameCompassApplication.class, args);
    }

}
```

---

## 2. `JogoController.java`
**Localização:** `src/main/java/com/ed/GameCompass/controller/JogoController.java`

O Controller é o componente responsável por interceptar as requisições HTTP enviadas pelo navegador (Front-End), processar os parâmetros, chamar os serviços necessários e retornar a View (HTML) correspondente com os dados.

### Explicação Linha por Linha:
```java
package com.ed.GameCompass.controller;

import com.ed.GameCompass.model.Jogo;
import com.ed.GameCompass.service.RecomendacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller // Indica ao Spring que esta classe é um Controlador Web MVC responsável por lidar com requisições HTTP
public class JogoController {

    // Dependência do serviço de recomendações (imutável)
    private final RecomendacaoService recomendacaoService;

    // Injeção de Dependência via Construtor: O Spring Boot instancia automaticamente o RecomendacaoService e o injeta aqui.
    public JogoController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    // Mapeia as requisições GET para a raiz ("/") e para ("/jogos")
    @GetMapping({"/", "/jogos"})
    public String listarJogos(Model model) {
        // Adiciona ao objeto Model (ponte entre Java e o Thymeleaf) a lista completa do catálogo de jogos obtida do serviço.
        model.addAttribute("jogos", recomendacaoService.getCatalogo());
        // Retorna o nome da página HTML ("index") localizada em src/main/resources/templates/index.html
        return "index";
    }

    // Mapeia a rota dinâmica para exibir recomendações de um jogo específico baseado no seu índice numérico na lista.
    @GetMapping("/jogos/{indice}/recomendacoes")
    public String listarRecomendacoes(@PathVariable int indice, Model model) {
        // Validação de segurança: verifica se o índice fornecido na URL é válido (não negativo e menor que o tamanho do catálogo).
        if (indice < 0 || indice >= recomendacaoService.getCatalogo().size()) {
            // Se o índice for inválido, lança uma exceção HTTP 404 (Not Found) informando que o jogo não foi encontrado.
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Jogo nao encontrado"
            );
        }

        // Obtém o jogo base correspondente ao índice na lista do catálogo.
        Jogo jogoBase = recomendacaoService.getCatalogo().get(indice);
        
        // Adiciona o jogo base ao Model para exibição na View.
        model.addAttribute("jogoBase", jogoBase);
        
        // Calcula e adiciona a lista de recomendações ordenadas por similaridade ao Model.
        model.addAttribute(
                "recomendacoes",
                recomendacaoService.ordenarPorSimilaridade(jogoBase)
        );

        // Retorna o nome da página HTML de recomendações ("recomendacoes") correspondente a recomendacoes.html
        return "recomendacoes";
    }
}
```

---

## 3. Estrutura de Dados (Grafos)

O pacote `com.ed.GameCompass.grafo` implementa uma estrutura de grafos genérica (`<TIPO>`) utilizada para modelar a rede de relacionamentos e similaridade entre os jogos.

### 3.1 `Aresta.java`
**Localização:** `src/main/java/com/ed/GameCompass/grafo/Aresta.java`

Representa a conexão (aresta) entre dois vértices (jogos) em um grafo, contendo um peso (pontuação de similaridade).

#### Explicação Linha por Linha:
```java
package com.ed.GameCompass.grafo;

public class Aresta<TIPO> { // Classe genérica que aceita qualquer tipo de dado (no projeto, TIPO = Jogo)

    private Double peso; // Peso da aresta (representa a pontuação numérica de similaridade entre os jogos)
    private Vertice<TIPO> inicio; // Vértice de origem da relação
    private Vertice<TIPO> fim; // Vértice de destino da relação

    // Construtor que inicializa a aresta com seu peso, vértice de início e vértice de fim.
    public Aresta (Double peso, Vertice<TIPO> inicio, Vertice<TIPO> fim) {
        this.peso = peso;
        this.inicio = inicio;
        this.fim = fim;
    }

    // Métodos Getter e Setter padrão para encapsulamento dos atributos (peso, inicio, fim).
    public Double getPeso () { return peso; }
    public void setPeso (Double peso) { this.peso = peso; }

    public Vertice<TIPO> getInicio () { return inicio; }
    public void setInicio (Vertice<TIPO> inicio) { this.inicio = inicio; }

    public Vertice<TIPO> getFim() { return fim; }
    public void setFim (Vertice<TIPO> fim) { this.fim = fim; }
}
```

---

### 3.2 `Vertice.java`
**Localização:** `src/main/java/com/ed/GameCompass/grafo/Vertice.java`

Representa um nó (vértice) dentro do grafo. Cada vértice armazena um objeto de dado (`Jogo`) e listas de arestas de entrada e de saída.

#### Explicação Linha por Linha:
```java
package com.ed.GameCompass.grafo;

import java.util.ArrayList;

public class Vertice<TIPO> {
    private TIPO dado; // O dado armazenado no vértice (no nosso caso, um objeto Jogo)
    private ArrayList<Aresta<TIPO>> arestasEntrada; // Lista de arestas que chegam a este vértice
    private ArrayList<Aresta<TIPO>> arestasSaida; // Lista de arestas que partem deste vértice

    // Construtor: inicializa o vértice com o valor e instancia as listas vazias de arestas.
    public Vertice(TIPO valor){
        this.dado = valor;
        this.arestasEntrada = new ArrayList<Aresta<TIPO>>();
        this.arestasSaida = new ArrayList<Aresta<TIPO>>();
    }

    // Getters e Setters para o atributo dado e listas de arestas.
    public TIPO getDado() { return dado; }
    public void setDado(TIPO dado) { this.dado = dado; }

    public ArrayList<Aresta<TIPO>> getArestasEntrada() { return arestasEntrada; }
    public void setArestasEntrada(ArrayList<Aresta<TIPO>> arestasEntrada) { this.arestasEntrada = arestasEntrada; }

    public ArrayList<Aresta<TIPO>> getArestasSaida() { return arestasSaida; }
    public void setArestasSaida(ArrayList<Aresta<TIPO>> arestasSaida) { this.arestasSaida = arestasSaida; }

    // Adiciona uma aresta à lista de entrada do vértice.
    public void adicionarArestaEntrada (Aresta<TIPO> aresta){
        this.arestasEntrada.add(aresta);
    }

    // Adiciona uma aresta à lista de saída do vértice.
    public void adicionarArestaSaida (Aresta<TIPO> aresta){
        this.arestasSaida.add(aresta);
    }
}
```

---

### 3.3 `Grafo.java`
**Localização:** `src/main/java/com/ed/GameCompass/grafo/Grafo.java`

Gerencia o conjunto de vértices e arestas que formam a rede completa de conexões.

#### Explicação Linha por Linha:
```java
package com.ed.GameCompass.grafo;

import java.util.ArrayList;

public class Grafo<TIPO> {
    private ArrayList<Vertice<TIPO>> vertices; // Lista contendo todos os vértices do grafo
    private ArrayList<Aresta<TIPO>> arestas; // Lista contendo todas as arestas do grafo

    // Construtor: inicializa as listas de vértices e arestas vazias.
    public Grafo(){
        this.vertices = new ArrayList<Vertice<TIPO>>();
        this.arestas = new ArrayList<Aresta<TIPO>>();
    }

    // Cria um novo vértice encapsulando o dado fornecido e o adiciona à lista de vértices.
    public void adicionarVertice(TIPO dado){
        Vertice<TIPO> novoVertice = new Vertice<TIPO>(dado);
        this.vertices.add(novoVertice);
    }

    // Cria uma aresta direcionada entre dois dados (início e fim) com um determinado peso.
    public void adicionarAresta (Double peso, TIPO dadoInicio, TIPO dadoFim) {
        // Localiza os vértices correspondentes aos dados de início e fim.
        Vertice<TIPO> inicio = this.getVertice(dadoInicio);
        Vertice<TIPO> fim = this.getVertice(dadoFim);
        
        // Instancia a aresta.
        Aresta<TIPO> aresta = new Aresta<TIPO>(peso, inicio, fim);
        
        // Registra a saída no vértice de origem e a entrada no vértice de destino.
        inicio.adicionarArestaSaida(aresta);
        fim.adicionarArestaEntrada(aresta);
        
        // Adiciona a aresta à lista geral do grafo.
        this.arestas.add(aresta);
    }

    // Busca e retorna o vértice que contém o dado especificado.
    public Vertice<TIPO> getVertice (TIPO dado){
        Vertice<TIPO> vertice = null;
        for(int i=0; i < this.vertices.size(); i++) {
            // Compara os dados utilizando o método .equals()
            if (this.vertices.get(i).getDado().equals(dado)) {
                vertice = this.vertices.get(i);
                break;
            }
        }
        return vertice;
    }
}
```

---

## 4. `RecomendacaoService.java`
**Localização:** `src/main/java/com/ed/GameCompass/service/RecomendacaoService.java`

O `RecomendacaoService` é o coração lógico da aplicação. Ele armazena o catálogo estático de jogos, constrói o grafo de similaridade, calcula as pontuações entre os jogos e gera as recomendações ordenadas.

### Explicação Linha por Linha:
```java
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

@Service // Anotação do Spring que define esta classe como um Componente de Serviço (regra de negócio)
public class RecomendacaoService {

    // Lista fixa (catálogo) contendo todos os jogos disponíveis na aplicação.
    private final List<Jogo> catalogo = List.of(
            new Jogo("The Witcher 3", "...", "RPG de mundo aberto...", LocalDate.of(2015, 5, 18), new String[]{"RPG", "Acao", "Aventura"}, "CD Projekt Red"),
            // ... (outros jogos cadastrados)
    );

    // Instancia o grafo tipado para o objeto Jogo.
    private final Grafo<Jogo> grafo = new Grafo<>();

    // Construtor do Service: executado automaticamente na inicialização, dispara a construção do grafo.
    public RecomendacaoService() {
        construirGrafo();
    }

    // Retorna o catálogo completo de jogos.
    public List<Jogo> getCatalogo() {
        return catalogo;
    }

    // Método privado responsável por popular o grafo com vértices e arestas de similaridade.
    private void construirGrafo() {
        // Passo 1: Cada jogo do catálogo vira um vértice no grafo.
        for (Jogo jogo : catalogo) {
            grafo.adicionarVertice(jogo);
        }

        // Passo 2: Compara cada par de jogos e cria relações nos dois sentidos.
        for (int i = 0; i < catalogo.size(); i++) {
            for (int j = i + 1; j < catalogo.size(); j++) {
                Jogo jogoAtual = catalogo.get(i);
                Jogo outroJogo = catalogo.get(j);
                
                // Calcula a pontuação de similaridade entre os dois jogos.
                int similaridade = calcularSimilaridade(jogoAtual, outroJogo);

                // Se houver similaridade (> 0), cria arestas bidirecionais entre eles.
                if (similaridade > 0) {
                    grafo.adicionarAresta((double) similaridade, jogoAtual, outroJogo);
                    grafo.adicionarAresta((double) similaridade, outroJogo, jogoAtual);
                }
            }
        }
    }

    // Calcula a pontuação de similaridade entre dois jogos com base em regras de negócio.
    public int calcularSimilaridade(Jogo jogoBase, Jogo jogoCandidato) {
        int pontuacao = 0;

        // Regra 1: Cada gênero compartilhado soma 1 ponto.
        for (int i = 0; i < jogoBase.getGeneros().length; i++) {
            for (int j = 0; j < jogoCandidato.getGeneros().length; j++) {
                if (jogoBase.getGeneros()[i].equals(jogoCandidato.getGeneros()[j])){
                    pontuacao++;
                    break;
                }
            }
        }

        // Regra 2: Se forem da mesma desenvolvedora, soma 3 pontos bônus.
        if (jogoBase.getDesenvolvedor().equals(jogoCandidato.getDesenvolvedor())){
            pontuacao += 3;
        }

        return pontuacao;
    }

    // Gera a lista de recomendações para um jogo base específico, ordenadas da maior para a menor pontuação.
    public List<Recomendacao> ordenarPorSimilaridade(Jogo jogoBase){
        ArrayList<Recomendacao> recomendacoes = new ArrayList<>();
        
        // Obtém o vértice correspondente ao jogo base no grafo.
        Vertice<Jogo> verticeBase = grafo.getVertice(jogoBase);

        // Percorre todas as arestas de saída do vértice base.
        for (Aresta<Jogo> aresta : verticeBase.getArestasSaida()) {
            Jogo jogoRecomendado = aresta.getFim().getDado(); // Jogo de destino
            int indiceRecomendado = catalogo.indexOf(jogoRecomendado); // Índice no catálogo original
            
            // Cria o objeto Recomendacao e adiciona à lista.
            recomendacoes.add(new Recomendacao(jogoRecomendado, aresta.getPeso().intValue(), indiceRecomendado));
        }

        // Ordena a lista de recomendações em ordem decrescente de pontuação (maior similaridade primeiro).
        recomendacoes.sort(
                Comparator.comparingInt(Recomendacao::getPontuacao).reversed());

        return recomendacoes;
    }
}
```

---

## 5. Comunicação Front-End ⇄ Controller

O projeto utiliza **Thymeleaf** como engine de templates no lado do servidor (Server-Side Rendering). Isso significa que as páginas HTML não são arquivos estáticos puros consumidos via API REST por um front-end em React/Angular; em vez disso, **o próprio Spring Boot processa o HTML, injeta os dados do Model e envia a página pronta para o navegador**.

### Fluxo detalhado da comunicação:

1. **Acesso à Página Inicial (`/` ou `/jogos`):**
   - O usuário digita `http://localhost:8080/` ou clica em "Explorar".
   - O navegador envia uma requisição **HTTP GET** para o `JogoController` no método `listarJogos(Model model)`.
   - O Controller chama `recomendacaoService.getCatalogo()`, obtém a lista de jogos e insere no objeto `Model` com a chave `"jogos"`.
   - O Spring renderiza o arquivo `index.html`.
   - No `index.html`, o Thymeleaf processa a expressão `th:each="jogo, stat : ${jogos}"`:
     ```html
     <div class="card" th:each="jogo, stat : ${jogos}">
         <h3 th:text="${jogo.nome}">Nome do Jogo</h3>
         <!-- ... -->
         <a class="btn" th:href="@{/jogos/{index}/recomendacoes(index=${stat.index})}">Ver Recomendações</a>
     </div>
     ```
     Cada card de jogo gera dinamicamente um link apontando para `/jogos/{index}/recomendacoes` (onde `{index}` é a posição do jogo no array).

2. **Acesso à Página de Recomendações (`/jogos/{indice}/recomendacoes`):**
   - O usuário clica em "Ver Recomendações" em um jogo (ex: índice `0`).
   - O navegador faz uma requisição **HTTP GET** para `/jogos/0/recomendacoes`.
   - O `JogoController` intercepta a requisição no método `listarRecomendacoes(@PathVariable int indice, Model model)`.
   - O Controller valida o índice, busca o `jogoBase` no catálogo, calcula as recomendações via `recomendacaoService.ordenarPorSimilaridade(jogoBase)` e adiciona ambos ao `Model` (`jogoBase` e `recomendacoes`).
   - O Spring renderiza o arquivo `recomendacoes.html`.
   - O Thymeleaf preenche os dados do jogo base e cria a grade (`grid`) com os 4 jogos mais recomendados utilizando `th:each`:
     ```html
     <a class="rec-card" th:each="rec, stat : ${recomendacoes}" th:if="${stat.index < 4}" th:href="@{/jogos/{index}/recomendacoes(index=${rec.indice})}">
         <h4 th:text="${rec.jogo.nome}">Nome</h4>
         <span class="similarity-badge" th:text="'Similaridade: ' + ${rec.pontuacao} + ' pts'"></span>
     </a>
     ```
   - O usuário recebe a página HTML completa e estilizada no navegador, podendo navegar recursivamente para recomendações de outros jogos.

---

## 6. Modelos da aplicação

### 6.1 `Jogo.java`
**Localização:** `src/main/java/com/ed/GameCompass/model/Jogo.java`

Representa um jogo do catálogo. A classe armazena o nome, a URL da capa, a descrição, a data opcional de lançamento, os gêneros e o desenvolvedor.

Os gêneros são usados pelo algoritmo de similaridade. A classe possui dois construtores: um com data de lançamento e outro sem data, que delega ao construtor completo usando `null`. Getters e setters permitem que o Thymeleaf acesse as propriedades por expressões como `${jogo.nome}`.

### 6.2 `Recomendacao.java`
**Localização:** `src/main/java/com/ed/GameCompass/model/Recomendacao.java`

Representa o resultado de uma recomendação. Ela reúne o jogo recomendado, sua pontuação de similaridade e o índice do jogo no catálogo. Esse índice é usado para montar o link da próxima página de recomendações.

---

## 7. Templates e identidade visual

### 7.1 `index.html`
**Localização:** `src/main/resources/templates/index.html`

É a página principal do catálogo. O controller fornece a lista no atributo `jogos`, e o Thymeleaf cria um card para cada item usando `th:each`.

Cada card apresenta capa, nome, desenvolvedor, gêneros, descrição, data de lançamento e um link para `/jogos/{indice}/recomendacoes`. O cabeçalho contém a imagem local `gamecompass-logo.png`, um separador visual e o texto estilizado “GameCompass”.

### 7.2 `recomendacoes.html`
**Localização:** `src/main/resources/templates/recomendacoes.html`

Exibe o jogo selecionado e, abaixo dele, até quatro recomendações. A lista recebida pelo atributo `recomendacoes` já chega ordenada pelo service; o template limita a exibição com `th:if="${stat.index < 4}"`.

As imagens das capas usam `onerror` para apresentar uma imagem alternativa quando a URL original não carrega. O cabeçalho e o favicon utilizam o recurso local `/images/gamecompass-logo.png`.

### 7.3 `gamecompass.css`
**Localização:** `src/main/resources/static/css/gamecompass.css`

É a folha de estilos própria da aplicação. O projeto não utiliza Bootstrap ou outro framework CSS. O arquivo define:

- Variáveis de cores da identidade visual.
- Cabeçalho, navegação e composição da logo.
- Layout responsivo do catálogo.
- Cards de jogos e recomendações.
- Página de detalhes do jogo base.
- Painéis laterais e rodapé.
- Efeitos de hover, sombras, bordas e gradientes.

### 7.4 Imagens e favicon
**Localização:** `src/main/resources/static/images/gamecompass-logo.png`

A imagem local é usada como marca no cabeçalho e como favicon nas duas páginas HTML. As capas dos jogos do catálogo são carregadas principalmente de URLs externas; por isso os templates possuem uma imagem de fallback para falhas de carregamento.

---

## 8. Configuração, execução e testes

### 8.1 `pom.xml`
**Localização:** `pom.xml`

O projeto é gerenciado pelo Maven e usa:

- Java 17 como versão configurada de compilação.
- `spring-boot-starter-web` para o servidor web e MVC.
- `spring-boot-starter-thymeleaf` para renderização dos templates.
- `spring-boot-starter-test` para testes automatizados.
- `spring-boot-maven-plugin` para empacotamento e execução da aplicação.

### 8.2 Execução

No Windows, o Maven Wrapper do projeto pode ser usado com:

```powershell
.\mvnw.cmd spring-boot:run
```

Depois, a aplicação fica disponível em `http://localhost:8080/`.

As rotas principais são:

| Método | Rota | Resultado |
|---|---|---|
| GET | `/` | Catálogo principal |
| GET | `/jogos` | Catálogo principal |
| GET | `/jogos/{indice}/recomendacoes` | Jogo base e recomendações |

Índices inválidos na rota de recomendações retornam HTTP 404.

### 8.3 Testes
**Localização:** `src/test/java/com/ed/GameCompass/GameCompassApplicationTests.java`

O teste anotado com `@SpringBootTest` verifica se o contexto da aplicação pode ser carregado corretamente, incluindo a configuração do Spring, o controller, o service e os templates necessários.

Para executá-lo no Windows:

```powershell
.\mvnw.cmd test
```

### 8.4 Observações sobre o grafo

O algoritmo compara cada par de jogos apenas uma vez. Quando a similaridade é maior que zero, o service adiciona duas arestas: uma do primeiro jogo para o segundo e outra no sentido inverso. Portanto, a classe `Grafo` armazena arestas direcionadas, mas o conjunto de relações usado pelo recomendador possui comportamento equivalente ao de um grafo não direcionado.

O peso de cada aresta é calculado assim:

```text
pontuação = quantidade de gêneros compartilhados
           + 3 pontos se os desenvolvedores forem iguais
```

As recomendações são ordenadas em ordem decrescente de pontuação, e o template exibe as quatro primeiras.
