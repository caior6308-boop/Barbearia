package com.jhon.barbearia.service;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResumo(
        long totalAgendamentos,
        long agendamentosHoje,
        long agendamentosSemana,
        long agendamentosMes,
        long totalClientes,
        long novosClientesMes,
        long cancelamentos,
        BigDecimal faturamentoHoje,
        BigDecimal faturamentoSemana,
        BigDecimal faturamentoMes,
        List<IndicadorRanking> servicosMaisSolicitados,
        List<IndicadorRanking> barbeirosComMaisAtendimentos,
        List<IndicadorFinanceiro> comissoesEstimadas,
        List<IndicadorRanking> horariosDeMaiorMovimento
) {
}
