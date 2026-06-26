package com.jhon.barbearia.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(
        name = "agendamentos",
        uniqueConstraints = @UniqueConstraint(name = "uk_agendamento_barbeiro_horario", columnNames = {"barbeiro_id", "data_hora"})
)
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private Barbeiro barbeiro;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 20)
    private String status = "Agendado";

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @ManyToMany
    @JoinTable(
            name = "agendamento_servicos",
            joinColumns = @JoinColumn(name = "agendamento_id"),
            inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicos;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }

    public BigDecimal getValorTotal() {
        if (this.servicos == null || this.servicos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return this.servicos.stream()
                .map(Servico::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
