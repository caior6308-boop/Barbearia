package com.jhon.barbearia.controller;

import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private final ClienteService clienteService;

    public PerfilController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/perfil")
    public String abrirPerfil(HttpSession session, Model model) {
        Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");
        if (clienteLogado == null) {
            return "redirect:/login";
        }

        model.addAttribute("cliente", clienteLogado);
        return "perfil";
    }

    @PostMapping("/perfil/atualizar")
    public String atualizarPerfil(@RequestParam String nome,
                                  @RequestParam String email,
                                  @RequestParam(required = false) String telefone,
                                  @RequestParam(required = false) String novaSenha,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");
        if (clienteLogado == null) {
            return "redirect:/login";
        }

        try {
            Cliente atualizado = clienteService.atualizarPerfil(clienteLogado.getId(), nome, email, telefone, novaSenha);
            session.setAttribute("usuarioLogado", atualizado);
            redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/perfil";
    }
}
