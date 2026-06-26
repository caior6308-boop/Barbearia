package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Agendamento;
import java.util.List;

public interface AgendamentoService {
    void realizarAgendamento(Agendamento agendamento) throws Exception;

    void cancelar(Long agendamentoId, Long clienteId) throws Exception;

    List<Agendamento> buscarPorBarbeiro(Long barbeiroId);

    List<Agendamento> buscarPorCliente(Long clienteId);
}
