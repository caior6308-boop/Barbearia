package com.jhon.barbearia.controller;

import com.jhon.barbearia.repository.BarbeiroRepository;
import com.jhon.barbearia.repository.ServicoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final ServicoRepository servicoRepository;
    private final BarbeiroRepository barbeiroRepository;

    public IndexController(ServicoRepository servicoRepository, BarbeiroRepository barbeiroRepository) {
        this.servicoRepository = servicoRepository;
        this.barbeiroRepository = barbeiroRepository;
    }

    @GetMapping("/")
    public String abrirPaginaInicial(Model model) {
        model.addAttribute("listaServicos", servicoRepository.findAll());
        model.addAttribute("listaBarbeiros", barbeiroRepository.findAll());
        return "Index";
    }
}
