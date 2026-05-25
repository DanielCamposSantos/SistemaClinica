package io.github.danielcampossantos.paciente;

import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.paciente.dto.PacienteGetResponse;
import io.github.danielcampossantos.paciente.dto.ProntuarioGetResponse;

import java.util.List;

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

    public List<PacienteGetResponse> findAll() {
        return repository.findAll();
    }

    public PacienteGetResponse findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Paciente não encontrado"));
    }


    public List<ProntuarioGetResponse> findAllProntuarios() {
        return repository.findAllProntuario();
    }

    public ProntuarioGetResponse findProntuarioByPacienteId(Long id) {
        return repository.findByIdProntuario(id)
                .orElseThrow(() -> new BadRequestException("Paciente não encontrado"));
    }

}
