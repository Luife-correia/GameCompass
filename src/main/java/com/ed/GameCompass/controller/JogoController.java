package com.ed.GameCompass.controller;

import com.ed.GameCompass.model.Jogo;
import com.ed.GameCompass.service.RecomendacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
//Controla as páginas relacionadas ao catálogo e às recomendações de jogos.
//O controller valida os parâmetros recebidos e encaminha os dados preparados
//pelo service para os templates Thymeleaf.
public class JogoController {

    // Serviço responsável pelo catálogo e pelo cálculo das recomendações.
    private final RecomendacaoService recomendacaoService;


    //Recebe o service por injeção de dependência do Spring.
    public JogoController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    //Exibe o catálogo principal de jogos.
    @GetMapping({"/", "/jogos"})
    public String listarJogos(Model model) {
        model.addAttribute("jogos", recomendacaoService.getCatalogo());
        return "index";
    }


     //Exibe o jogo selecionado e suas recomendações ordenadas.
     //O índice é validado antes do acesso à lista para transformar entradas
     //inválidas em uma resposta HTTP 404, evitando erro de índice.
    @GetMapping("/jogos/{indice}/recomendacoes")
    public String listarRecomendacoes(@PathVariable int indice, Model model) {
        if (indice < 0 || indice >= recomendacaoService.getCatalogo().size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Jogo nao encontrado"
            );
        }

        Jogo jogoBase = recomendacaoService.getCatalogo().get(indice);
        model.addAttribute("jogoBase", jogoBase);
        model.addAttribute(
                "recomendacoes",
                recomendacaoService.ordenarPorSimilaridade(jogoBase)
        );

        return "recomendacoes";
    }
}
