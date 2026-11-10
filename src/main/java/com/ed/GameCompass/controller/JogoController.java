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
public class JogoController {

    private final RecomendacaoService recomendacaoService;

    public JogoController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    @GetMapping({"/", "/jogos"})
    public String listarJogos(Model model) {
        model.addAttribute("jogos", recomendacaoService.getCatalogo());
        return "index";
    }

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
