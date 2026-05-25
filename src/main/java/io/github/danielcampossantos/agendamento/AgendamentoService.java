package io.github.danielcampossantos.agendamento;

import io.github.danielcampossantos.agendamento.dto.AgendamentoGetResponse;

import java.util.List;
import java.util.Optional;

public class AgendamentoService {
    private static AgendamentoService instance;
    private final AgendamentoRepository repository;

    public static AgendamentoService getInstance() {
        if (instance == null) {
            instance = new AgendamentoService();
        }
        return instance;
    }

    private AgendamentoService() {
        this.repository = AgendamentoRepository.getInstance();
    }

    public List<AgendamentoGetResponse> findAll() {
        return repository.findAll();
    }

    public Optional<AgendamentoGetResponse> findById(Long id) {
        return repository.findById(id);
    }

    public List<AgendamentoGetResponse> findByPacienteId(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    public List<AgendamentoGetResponse> findByMes(int mes, int ano) {
        return repository.findByMes(mes, ano);
    }
}