package com.jhon.barbearia.controller;

import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.service.ClienteService;
import java.security.SecureRandom;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RecuperacaoController {

    private final JavaMailSender mailSender;
    private final ClienteService clienteService;
    private final SecureRandom secureRandom = new SecureRandom();

    public RecuperacaoController(JavaMailSender mailSender, ClienteService clienteService) {
        this.mailSender = mailSender;
        this.clienteService = clienteService;
    }

    @PostMapping("/recuperar-senha")
    public String recuperarSenha(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteService.buscarPorEmail(email)
                    .orElseThrow(() -> new Exception("E-mail nao encontrado em nosso sistema."));
            String senhaTemporaria = gerarSenhaTemporaria();
            clienteService.redefinirSenha(email, senhaTemporaria);

            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(email);
            mensagem.setSubject("Jhon Barbearia - Recuperacao de Senha");
            mensagem.setText("Ola, " + cliente.getNome() + "!\n\n"
                    + "Sua nova senha temporaria de acesso e: " + senhaTemporaria
                    + "\n\nFaca login com esta senha e altere-a em Meu Perfil.");

            mailSender.send(mensagem);
            redirectAttributes.addFlashAttribute("sucesso", "Um e-mail com sua nova senha foi enviado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/login";
    }

    private String gerarSenhaTemporaria() {
        int numero = secureRandom.nextInt(1_000_000);
        return String.format("%06d", numero);
    }
}
