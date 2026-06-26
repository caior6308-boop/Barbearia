package com.jhon.barbearia.repository;

import com.jhon.barbearia.domain.Agendamento;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByBarbeiroId(Long barbeiroId);

    List<Agendamento> findAllByOrderByDataHoraDesc();

    List<Agendamento> findByDataHoraBetweenOrderByDataHoraAsc(LocalDateTime inicio, LocalDateTime fim);

    List<Agendamento> findByClienteIdOrderByDataHoraDesc(Long clienteId);

    List<Agendamento> findByBarbeiroIdAndDataHoraBetweenAndStatusNot(
            Long barbeiroId,
            LocalDateTime inicio,
            LocalDateTime fim,
            String status
    );

    boolean existsByBarbeiroIdAndDataHoraAndStatusNot(Long barbeiroId, LocalDateTime dataHora, String status);

    long countByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    long countByStatus(String status);

    long countByStatusAndDataHoraBetween(String status, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            select coalesce(sum(s.preco), 0)
            from Agendamento a
            join a.servicos s
            where a.status <> 'Cancelado' and a.dataHora between :inicio and :fim
            """)
    java.math.BigDecimal somarFaturamento(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            select s.nome, count(s.id)
            from Agendamento a
            join a.servicos s
            where a.status <> 'Cancelado'
            group by s.nome
            order by count(s.id) desc
            """)
    List<Object[]> servicosMaisSolicitados();

    @Query("""
            select b.nome, count(a.id)
            from Agendamento a
            join a.barbeiro b
            where a.status <> 'Cancelado'
            group by b.nome
            order by count(a.id) desc
            """)
    List<Object[]> barbeirosComMaisAtendimentos();

    @Query("""
            select b.nome, coalesce(sum(s.preco), 0)
            from Agendamento a
            join a.barbeiro b
            join a.servicos s
            where a.status <> 'Cancelado'
            group by b.nome
            order by coalesce(sum(s.preco), 0) desc
            """)
    List<Object[]> faturamentoPorBarbeiro();

    @Query("""
            select hour(a.dataHora), count(a.id)
            from Agendamento a
            where a.status <> 'Cancelado'
            group by hour(a.dataHora)
            order by count(a.id) desc
            """)
    List<Object[]> horariosDeMaiorMovimento();
}
