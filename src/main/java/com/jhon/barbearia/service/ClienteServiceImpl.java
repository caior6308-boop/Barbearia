package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.repository.ClienteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteServiceImpl(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void salvar(Cliente cliente) throws Exception {
        String emailNormalizado = normalizarEmail(cliente.getEmail());
        cliente.setEmail(emailNormalizado);

        if (cliente.getId() == null && clienteRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new Exception("Ja existe um cliente cadastrado com este e-mail.");
        }

        if (cliente.getId() == null) {
            validarSenha(cliente.getSenha());
            cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

            if (clienteRepository.count() == 0) {
                cliente.setPapel("ADMIN");
            } else if (cliente.getPapel() == null || cliente.getPapel().isBlank()) {
                cliente.setPapel("CLIENTE");
            }
        }

        clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public Cliente atualizarPerfil(Long clienteId, String nome, String email, String telefone, String novaSenha) throws Exception {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new Exception("Cliente nao encontrado."));

        String emailNormalizado = normalizarEmail(email);
        Optional<Cliente> clienteComEmail = clienteRepository.findByEmailIgnoreCase(emailNormalizado);
        if (clienteComEmail.isPresent() && !clienteComEmail.get().getId().equals(clienteId)) {
            throw new Exception("Este e-mail ja esta em uso por outro cliente.");
        }

        cliente.setNome(nome);
        cliente.setEmail(emailNormalizado);
        cliente.setTelefone(telefone);

        if (novaSenha != null && !novaSenha.isBlank()) {
            validarSenha(novaSenha);
            cliente.setSenha(passwordEncoder.encode(novaSenha));
        }

        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional
    public void redefinirSenha(String email, String novaSenha) throws Exception {
        Cliente cliente = clienteRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new Exception("E-mail nao encontrado em nosso sistema."));
        validarSenha(novaSenha);
        cliente.setSenha(passwordEncoder.encode(novaSenha));
        clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmailIgnoreCase(normalizarEmail(email));
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public boolean autenticar(String email, String senha) {
        return clienteRepository.findByEmailIgnoreCase(normalizarEmail(email))
                .filter(cliente -> cliente.getAtivo() == null || Boolean.TRUE.equals(cliente.getAtivo()))
                .map(cliente -> passwordEncoder.matches(senha, cliente.getSenha()))
                .orElse(false);
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private void validarSenha(String senha) throws Exception {
        if (senha == null || senha.length() < 6) {
            throw new Exception("A senha deve ter pelo menos 6 caracteres.");
        }
    }
}
