package com.jhon.barbearia.controller;

import com.jhon.barbearia.repository.AgendamentoRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgendamentoApiController {

    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoApiController(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @GetMapping("/api/agendamentos/horarios-disponiveis")
    public List<String> horariosDisponiveis(
            @RequestParam Long barbeiroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        Set<LocalTime> ocupados = new HashSet<>(agendamentoRepository
                .findByBarbeiroIdAndDataHoraBetweenAndStatusNot(
                        barbeiroId,
                        data.atStartOfDay(),
                        data.atTime(LocalTime.MAX),
                        "Cancelado"
                )
                .stream()
                .map(agendamento -> agendamento.getDataHora().toLocalTime())
                .toList());

        // Por enquanto usamos a agenda padrao da barbearia; depois pode vir da tabela barbeiro_escalas.
        return gerarGradeDoDia(data).stream()
                .filter(horario -> !ocupados.contains(horario))
                .filter(horario -> data.isAfter(LocalDate.now()) || horario.isAfter(LocalTime.now()))
                .map(horario -> horario.toString().substring(0, 5))
                .toList();
    }

    private List<LocalTime> gerarGradeDoDia(LocalDate data) {
        if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return List.of();
        }

        LocalTime inicio = LocalTime.of(9, 0);
        LocalTime fim = switch (data.getDayOfWeek()) {
            case MONDAY -> LocalTime.of(19, 0);
            case TUESDAY, SATURDAY -> LocalTime.of(20, 0);
            default -> LocalTime.of(21, 0);
        };

        return java.util.stream.Stream.iterate(inicio, horario -> horario.plusMinutes(30))
                .limit(40)
                .filter(horario -> horario.isBefore(fim))
                .toList();
    }
}
