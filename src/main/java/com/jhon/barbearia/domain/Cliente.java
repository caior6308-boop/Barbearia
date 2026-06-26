package com.jhon.barbearia.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(nullable = false)
    private String senha;

    @Column(length = 20)
    private String papel = "CLIENTE";

    @Column
    private Boolean ativo = true;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
        if (papel == null || papel.isBlank()) {
            papel = "CLIENTE";
        }
        if (ativo == null) {
            ativo = true;
        }
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(papel) || "PROPRIETARIO".equalsIgnoreCase(papel);
    }

    public boolean isEquipe() {
        return isAdmin() || "BARBEIRO".equalsIgnoreCase(papel);
    }
}
