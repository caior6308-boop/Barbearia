package com.jhon.barbearia.repository;

import com.jhon.barbearia.domain.Cliente;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByPapelIn(Collection<String> papeis);

    long countByCriadoEmBetween(LocalDateTime inicio, LocalDateTime fim);
}
