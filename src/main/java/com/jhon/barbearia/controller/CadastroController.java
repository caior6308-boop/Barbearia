package com.jhon.barbearia.controller;

import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CadastroController {

    private final ClienteService clienteService;

    public CadastroController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/cadastro")
    public String abrirTelaCadastro(Model model) {
        model.addAttribute("abrirAbaCadastro", true);
        return "login";
    }

    @PostMapping("/cadastro")
    public String salvarNovoCliente(Cliente novoCliente, Model model) {
        try {
            clienteService.salvar(novoCliente);
            model.addAttribute("sucesso", "Cadastro realizado com sucesso. Faca seu login.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("abrirAbaCadastro", true);
            return "login";
        }
    }
}
