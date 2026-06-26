package com.jhon.barbearia.controller;

import com.jhon.barbearia.domain.Barbeiro;
import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.domain.Servico;
import com.jhon.barbearia.repository.AgendamentoRepository;
import com.jhon.barbearia.repository.BarbeiroRepository;
import com.jhon.barbearia.repository.ClienteRepository;
import com.jhon.barbearia.repository.ServicoRepository;
import com.jhon.barbearia.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final DashboardService dashboardService;
    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;
    private final ClienteRepository clienteRepository;

    public AdminController(
            DashboardService dashboardService,
            AgendamentoRepository agendamentoRepository,
            BarbeiroRepository barbeiroRepository,
            ServicoRepository servicoRepository,
            ClienteRepository clienteRepository
    ) {
        this.dashboardService = dashboardService;
        this.agendamentoRepository = agendamentoRepository;
        this.barbeiroRepository = barbeiroRepository;
        this.servicoRepository = servicoRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/admin")
    public String dashboard(HttpSession session, Model model) {
        if (!temAcessoAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("resumo", dashboardService.montarResumo());
        model.addAttribute("agendamentos", agendamentoRepository.findAllByOrderByDataHoraDesc());
        model.addAttribute("barbeiros", barbeiroRepository.findAll());
        model.addAttribute("servicos", servicoRepository.findAll());
        model.addAttribute("clientes", clienteRepository.findAll());
        return "admin/dashboard";
    }

    @PostMapping("/admin/barbeiros/salvar")
    public String salvarBarbeiro(@RequestParam(required = false) Long id,
                                 @RequestParam String nome,
                                 @RequestParam(required = false) String especialidade,
                                 @RequestParam(required = false) String foto,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!temAcessoAdmin(session)) {
            return "redirect:/login";
        }

        Barbeiro barbeiro = id == null ? new Barbeiro() : barbeiroRepository.findById(id).orElse(new Barbeiro());
        barbeiro.setNome(nome);
        barbeiro.setEspecialidade(especialidade);
        barbeiro.setFoto(foto);
        barbeiroRepository.save(barbeiro);
        redirectAttributes.addFlashAttribute("sucesso", "Barbeiro salvo.");
        return "redirect:/admin#barbeiros";
    }

    @PostMapping("/admin/barbeiros/excluir")
    public String excluirBarbeiro(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!temAcessoAdmin(session)) {
            return "redirect:/login";
        }
        try {
            barbeiroRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Barbeiro excluido.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Nao foi possivel excluir barbeiro com agendamentos vinculados.");
        }
        return "redirect:/admin#barbeiros";
    }

    @PostMapping("/admin/servicos/salvar")
    public String salvarServico(@RequestParam(required = false) Long id,
                                @RequestParam String nome,
                                @RequestParam BigDecimal preco,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!temAcessoAdmin(session)) {
            return "redirect:/login";
        }

        Servico servico = id == null ? new Servico() : servicoRepository.findById(id).orElse(new Servico());
        servico.setNome(nome);
        servico.setPreco(preco);
        servicoRepository.save(servico);
        redirectAttributes.addFlashAttribute("sucesso", "Servico salvo.");
        return "redirect:/admin#servicos";
    }

    @PostMapping("/admin/servicos/excluir")
    public String excluirServico(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!temAcessoAdmin(session)) {
            return "redirect:/login";
        }
        try {
            servicoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Servico excluido.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Nao foi possivel excluir servico com agendamentos vinculados.");
        }
        return "redirect:/admin#servicos";
    }

    @PostMapping("/admin/clientes/permissao")
    public String atualizarPermissao(@RequestParam Long id,
                                     @RequestParam String papel,
                                     @RequestParam(defaultValue = "false") boolean ativo,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (!temAcessoAdmin(session)) {
            return "redirect:/login";
        }

        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setPapel(papel);
            cliente.setAtivo(ativo);
            clienteRepository.save(cliente);
        });
        redirectAttributes.addFlashAttribute("sucesso", "Permissao atualizada.");
        return "redirect:/admin#clientes";
    }

    private boolean temAcessoAdmin(HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");
        return cliente != null && cliente.isAdmin();
    }
}
