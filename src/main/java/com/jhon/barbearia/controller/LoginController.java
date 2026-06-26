package com.jhon.barbearia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String abrirTelaLogin(@RequestParam(required = false) String erro, Model model) {
        if (erro != null) {
            model.addAttribute("erro", "E-mail ou senha invalidos. Tente novamente.");
        }
        return "login";
    }
}
