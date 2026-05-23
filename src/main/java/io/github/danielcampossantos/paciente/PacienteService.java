package io.github.danielcampossantos.paciente;

import io.github.danielcampossantos.domain.Paciente;
import io.github.danielcampossantos.paciente.dto.Prontuario;

import java.util.List;
import java.util.Optional;

public class PacienteService {
    private static PacienteService instance;

    private final PacienteRepository repository;

    private PacienteService() {
        repository = PacienteRepository.getInstance();
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

    public Optional<Prontuario> findProntuarioByPacienteId(long id) {
        return repository.getProntuarioByPacienteId(id);
    }

    public Paciente save(Paciente paciente) {
        return repository.save(paciente);
    }
}
