package com.jhon.barbearia.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "barbeiros")
public class Barbeiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 100)
    private String especialidade;

    @Column(length = 200)
    private String foto;
}