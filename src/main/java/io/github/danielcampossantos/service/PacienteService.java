package io.github.danielcampossantos.service;

import io.github.danielcampossantos.domain.Paciente;
import io.github.danielcampossantos.repository.PacienteHardCodedRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class PacienteService {
    private static PacienteService instance;

    private final PacienteHardCodedRepository repository;
    private PacienteService() {
        repository = PacienteHardCodedRepository.getInstance();
    }
    public static PacienteService getInstance() {
        if (instance == null) {
            instance = new PacienteService();
        }
        return instance;
    }

    public List<Paciente> findAll() {
        return repository.findAll();
    }
}
