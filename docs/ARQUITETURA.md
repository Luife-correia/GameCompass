# Arquitetura do GameCompass

O projeto usa uma separacao em camadas:

- **Model**: representa os dados dos jogos e das recomendacoes.
- **Controller**: recebe as requisicoes HTTP e prepara os dados para o Thymeleaf.
- **Service**: calcula a similaridade e monta as recomendacoes.
- **Grafo**: representa os relacionamentos entre jogos.

## Fluxo de uma recomendacao

1. O usuario acessa `/jogos`.
2. O `JogoController` envia o catalogo para o template `index.html`.
3. O usuario escolhe um jogo.
4. O navegador acessa `/jogos/{indice}/recomendacoes`.
5. O controller busca o jogo base no catalogo.
6. O `RecomendacaoService` consulta os vizinhos desse jogo no grafo.
7. Os vizinhos sao ordenados pela pontuacao.
8. O resultado e enviado para `recomendacoes.html`.

## Controller

Arquivo: `src/main/java/com/ed/GameCompass/controller/JogoController.java`

### Construtor

Recebe um `RecomendacaoService` pelo construtor. O Spring cria o service e o injeta no controller.
Isso evita que o controller precise criar o service manualmente e deixa cada classe responsavel por sua propria funcao.

### `listarJogos(Model model)`

Atende as rotas `/` e `/jogos`.

1. Busca o catalogo usando `recomendacaoService.getCatalogo()`.
2. Adiciona o catalogo ao `Model` com o nome `jogos`.
3. Retorna `index`, que representa o arquivo `index.html` dentro de `templates`.

O objeto `Model` carrega dados do Java para o Thymeleaf.

### `listarRecomendacoes(int indice, Model model)`

Atende a rota `/jogos/{indice}/recomendacoes`.

1. Recebe o indice do jogo pela URL.
2. Valida se o indice existe no catalogo.
3. Busca o jogo selecionado.
4. Chama `ordenarPorSimilaridade`.
5. Envia o jogo base e a lista de recomendacoes para o `Model`.
6. Retorna `recomendacoes`, que representa `recomendacoes.html`.

Quando o indice nao existe, o metodo interrompe a requisicao com HTTP 404.

## Service

Arquivo: `src/main/java/com/ed/GameCompass/service/RecomendacaoService.java`

### `getCatalogo()`

Retorna a lista fixa de jogos usada pelo projeto. O controller usa esse metodo
para exibir os jogos e para localizar o jogo escolhido pelo indice.

### `construirGrafo()`

Monta a estrutura de relacionamentos:

1. Cada jogo do catalogo vira um vertice.
2. Cada par de jogos e comparado.
3. Se a similaridade for maior que zero, sao criadas duas arestas.
4. O peso das arestas recebe a pontuacao de similaridade.

As duas arestas representam uma relacao nos dois sentidos: um jogo pode recomendar
o outro e vice-versa.

### `calcularSimilaridade(Jogo jogoBase, Jogo jogoCandidato)`

Calcula a pontuacao entre dois jogos:

- cada genero compartilhado soma 1 ponto;
- o mesmo desenvolvedor soma 3 pontos.

O metodo compara todos os generos do jogo base com todos os generos do candidato,
por isso a ordem dos generos nos arrays nao altera o resultado.

### `ordenarPorSimilaridade(Jogo jogoBase)`

Gera a lista final de recomendacoes:

1. Localiza o vertice do jogo base.
2. Percorre as arestas que saem desse vertice.
3. Usa o jogo no final de cada aresta como candidato.
4. Converte o peso da aresta em uma `Recomendacao`.
5. Ordena pela pontuacao, da maior para a menor.

## Model

### `Jogo`

Representa um jogo do catalogo. Seus atributos sao:

- `nome`: nome do jogo;
- `capa`: caminho ou nome da imagem;
- `descricao`: texto descritivo;
- `generos`: generos associados;
- `desenvolvedor`: empresa responsavel pelo jogo.

Os getters permitem consultar os dados e os setters permitem altera-los.

### `Recomendacao`

Relaciona um objeto `Jogo` com sua pontuacao de similaridade.
A pontuacao fica nessa classe porque ela depende do jogo base escolhido.
O mesmo jogo pode ter pontuacoes diferentes quando comparado com jogos base diferentes.

## Pacote `grafo`

### `Grafo<TIPO>`

Classe generica que armazena vertices e arestas.

#### Construtor

Inicializa as listas internas de vertices e arestas vazias.

#### `adicionarVertice(TIPO dado)`

Cria um `Vertice` com o dado recebido e adiciona esse vertice ao grafo.
No projeto, o dado e um objeto `Jogo`.

#### `adicionarAresta(Double peso, TIPO dadoInicio, TIPO dadoFim)`

Cria uma aresta entre dois dados:

1. Localiza o vertice inicial.
2. Localiza o vertice final.
3. Cria a aresta com o peso informado.
4. Registra a aresta como saida no vertice inicial.
5. Registra a aresta como entrada no vertice final.
6. Armazena a aresta na lista geral do grafo.

#### `getVertice(TIPO dado)`

Procura um vertice pelo dado armazenado e retorna o primeiro encontrado.
Se nao encontrar, retorna `null`.

### `Vertice<TIPO>`

Representa um ponto do grafo.

- `dado`: objeto armazenado no vertice;
- `arestasEntrada`: relacionamentos que chegam ao vertice;
- `arestasSaida`: relacionamentos que partem do vertice.

#### `adicionarArestaEntrada(Aresta<TIPO> aresta)`

Registra uma aresta que chega ao vertice.

#### `adicionarArestaSaida(Aresta<TIPO> aresta)`

Registra uma aresta que parte do vertice.

Os getters permitem que o service percorra os relacionamentos para encontrar jogos recomendados.

### `Aresta<TIPO>`

Representa um relacionamento entre dois vertices.

- `peso`: valor do relacionamento;
- `inicio`: vertice de origem;
- `fim`: vertice de destino.

No GameCompass, o peso e a pontuacao de similaridade entre dois jogos.
Os getters permitem descobrir o peso e o jogo que esta no destino da relacao.
