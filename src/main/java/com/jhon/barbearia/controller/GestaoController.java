package com.jhon.barbearia.controller;

import com.jhon.barbearia.repository.AgendamentoRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GestaoController {

    private final AgendamentoRepository agendamentoRepository;

    public GestaoController(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @GetMapping("/gestao/agenda")
    public String agendaDoDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model
    ) {
        LocalDate dataConsulta = data == null ? LocalDate.now() : data;
        model.addAttribute("section", "agenda");
        model.addAttribute("dataSelecionada", dataConsulta);
        model.addAttribute("agendamentos", agendamentoRepository.findByDataHoraBetweenOrderByDataHoraAsc(
                dataConsulta.atStartOfDay(),
                dataConsulta.atTime(LocalTime.MAX)
        ));
        return "gestao/agenda";
    }
}
