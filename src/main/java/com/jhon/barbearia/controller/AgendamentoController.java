package com.jhon.barbearia.controller;

import com.jhon.barbearia.domain.Agendamento;
import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.repository.BarbeiroRepository;
import com.jhon.barbearia.repository.ServicoRepository;
import com.jhon.barbearia.service.AgendamentoService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AgendamentoController {

    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;
    private final AgendamentoService agendamentoService;

    public AgendamentoController(
            BarbeiroRepository barbeiroRepository,
            ServicoRepository servicoRepository,
            AgendamentoService agendamentoService
    ) {
        this.barbeiroRepository = barbeiroRepository;
        this.servicoRepository = servicoRepository;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping("/agendamento")
    public String abrirTelaAgendamento(@RequestParam Long barbeiroId, Model model) {
        return barbeiroRepository.findById(barbeiroId)
                .map(barbeiro -> {
                    model.addAttribute("barbeiro", barbeiro);
                    model.addAttribute("listaServicos", servicoRepository.findAll());
                    return "agendamento";
                })
                .orElse("redirect:/");
    }

    @PostMapping("/agendamento/salvar")
    public String salvarAgendamento(
            @RequestParam Long barbeiroId,
            @RequestParam List<Long> servicosIds,
            @RequestParam String data,
            @RequestParam String hora,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");
        if (clienteLogado == null) {
            return "redirect:/login";
        }

        try {
            Agendamento novoAgendamento = new Agendamento();
            novoAgendamento.setCliente(clienteLogado);
            novoAgendamento.setBarbeiro(barbeiroRepository.getReferenceById(barbeiroId));
            novoAgendamento.setServicos(servicosIds.stream()
                    .map(servicoRepository::getReferenceById)
                    .toList());
            novoAgendamento.setDataHora(LocalDateTime.parse(data + "T" + hora));

            agendamentoService.realizarAgendamento(novoAgendamento);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Seu horario foi agendado com sucesso.");
            return "redirect:/";
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Data ou horario invalido.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/agendamento?barbeiroId=" + barbeiroId;
    }

    @GetMapping("/meus-agendamentos")
    public String abrirMeusAgendamentos(HttpSession session, Model model) {
        Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");
        if (clienteLogado == null) {
            return "redirect:/login";
        }

        model.addAttribute("meusAgendamentos", agendamentoService.buscarPorCliente(clienteLogado.getId()));
        return "meus-agendamentos";
    }

    @PostMapping("/meus-agendamentos/cancelar")
    public String cancelarAgendamento(@RequestParam Long agendamentoId,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");
        if (clienteLogado == null) {
            return "redirect:/login";
        }

        try {
            agendamentoService.cancelar(agendamentoId, clienteLogado.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Agendamento cancelado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/meus-agendamentos";
    }
}
