package com.jhon.barbearia.service;

import com.jhon.barbearia.repository.AgendamentoRepository;
import com.jhon.barbearia.repository.ClienteRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;

    public DashboardServiceImpl(AgendamentoRepository agendamentoRepository, ClienteRepository clienteRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public DashboardResumo montarResumo() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioHoje = hoje.atStartOfDay();
        LocalDateTime fimHoje = hoje.atTime(LocalTime.MAX);
        LocalDateTime inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime fimSemana = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);
        LocalDateTime inicioMes = hoje.withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth()).atTime(LocalTime.MAX);

        return new DashboardResumo(
                agendamentoRepository.count(),
                agendamentoRepository.countByDataHoraBetween(inicioHoje, fimHoje),
                agendamentoRepository.countByDataHoraBetween(inicioSemana, fimSemana),
                agendamentoRepository.countByDataHoraBetween(inicioMes, fimMes),
                clienteRepository.count(),
                clienteRepository.countByCriadoEmBetween(inicioMes, fimMes),
                agendamentoRepository.countByStatus("Cancelado"),
                valorSeguro(agendamentoRepository.somarFaturamento(inicioHoje, fimHoje)),
                valorSeguro(agendamentoRepository.somarFaturamento(inicioSemana, fimSemana)),
                valorSeguro(agendamentoRepository.somarFaturamento(inicioMes, fimMes)),
                converterRanking(agendamentoRepository.servicosMaisSolicitados()),
                converterRanking(agendamentoRepository.barbeirosComMaisAtendimentos()),
                converterComissoes(agendamentoRepository.faturamentoPorBarbeiro()),
                converterRanking(agendamentoRepository.horariosDeMaiorMovimento()).stream()
                        .map(item -> new IndicadorRanking(item.nome() + "h", item.total()))
                        .toList()
        );
    }

    private BigDecimal valorSeguro(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private List<IndicadorRanking> converterRanking(List<Object[]> linhas) {
        return linhas.stream()
                .limit(8)
                .map(linha -> new IndicadorRanking(String.valueOf(linha[0]), ((Number) linha[1]).longValue()))
                .toList();
    }

    private List<IndicadorFinanceiro> converterComissoes(List<Object[]> linhas) {
        return linhas.stream()
                .limit(8)
                .map(linha -> new IndicadorFinanceiro(
                        String.valueOf(linha[0]),
                        ((BigDecimal) linha[1]).multiply(new BigDecimal("0.40"))
                ))
                .toList();
    }
}
