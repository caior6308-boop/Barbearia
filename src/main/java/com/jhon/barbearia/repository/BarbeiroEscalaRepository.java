package com.jhon.barbearia.repository;

import com.jhon.barbearia.domain.BarbeiroEscala;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarbeiroEscalaRepository extends JpaRepository<BarbeiroEscala, Long> {
    List<BarbeiroEscala> findByBarbeiroId(Long barbeiroId);
}
