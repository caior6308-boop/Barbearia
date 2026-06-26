package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Agendamento;
import com.jhon.barbearia.domain.Barbeiro;
import com.jhon.barbearia.domain.Servico;
import com.jhon.barbearia.repository.AgendamentoRepository;
import com.jhon.barbearia.repository.BarbeiroRepository;
import com.jhon.barbearia.repository.ServicoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;

    public AgendamentoServiceImpl(
            AgendamentoRepository agendamentoRepository,
            BarbeiroRepository barbeiroRepository,
            ServicoRepository servicoRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.barbeiroRepository = barbeiroRepository;
        this.servicoRepository = servicoRepository;
    }

    @Override
    @Transactional
    public void realizarAgendamento(Agendamento novoAgendamento) throws Exception {
        if (novoAgendamento.getCliente() == null || novoAgendamento.getCliente().getId() == null) {
            throw new Exception("Faca login antes de agendar.");
        }
        if (novoAgendamento.getDataHora() == null || !novoAgendamento.getDataHora().isAfter(LocalDateTime.now())) {
            throw new Exception("Escolha uma data e horario futuros.");
        }
        if (novoAgendamento.getServicos() == null || novoAgendamento.getServicos().isEmpty()) {
            throw new Exception("Selecione pelo menos um servico.");
        }

        Long barbeiroId = novoAgendamento.getBarbeiro().getId();
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new Exception("Barbeiro nao encontrado."));

        List<Long> servicosIds = novoAgendamento.getServicos().stream()
                .map(Servico::getId)
                .toList();
        List<Servico> servicos = servicoRepository.findAllById(servicosIds);
        if (servicos.size() != servicosIds.size()) {
            throw new Exception("Um ou mais servicos selecionados nao existem.");
        }

        boolean horarioOcupado = agendamentoRepository.existsByBarbeiroIdAndDataHoraAndStatusNot(
                barbeiroId,
                novoAgendamento.getDataHora(),
                "Cancelado"
        );
        if (horarioOcupado) {
            throw new Exception("Este horario ja esta ocupado para este barbeiro. Por favor, escolha outro.");
        }

        novoAgendamento.setBarbeiro(barbeiro);
        novoAgendamento.setServicos(servicos);
        novoAgendamento.setStatus("Agendado");
        agendamentoRepository.save(novoAgendamento);
    }

    @Override
    @Transactional
    public void cancelar(Long agendamentoId, Long clienteId) throws Exception {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new Exception("Agendamento nao encontrado."));
        if (!agendamento.getCliente().getId().equals(clienteId)) {
            throw new Exception("Este agendamento nao pertence ao cliente logado.");
        }
        if (!agendamento.getDataHora().isAfter(LocalDateTime.now())) {
            throw new Exception("Nao e possivel cancelar um atendimento que ja passou.");
        }
        agendamento.setStatus("Cancelado");
        agendamentoRepository.save(agendamento);
    }

    @Override
    public List<Agendamento> buscarPorBarbeiro(Long barbeiroId) {
        return agendamentoRepository.findByBarbeiroId(barbeiroId);
    }

    @Override
    public List<Agendamento> buscarPorCliente(Long clienteId) {
        return agendamentoRepository.findByClienteIdOrderByDataHoraDesc(clienteId);
    }
}
