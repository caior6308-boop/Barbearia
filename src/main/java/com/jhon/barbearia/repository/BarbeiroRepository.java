package com.jhon.barbearia.repository;

import com.jhon.barbearia.domain.Barbeiro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {
}
