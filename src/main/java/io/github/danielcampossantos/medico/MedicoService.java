package io.github.danielcampossantos.medico;


import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.medico.dto.MedicoGetResponse;

import java.util.List;

public class MedicoService {
    private static MedicoService instance;

    private final MedicoRepository repository;

    private MedicoService() {
        repository = MedicoRepository.getInstance();
    }

    public static MedicoService getInstance() {
        if (instance == null) {
            instance = new MedicoService();
        }
        return instance;
    }

    public List<MedicoGetResponse> findAll() {
        return repository.findAll();
    }

    public MedicoGetResponse findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Medico não encontrado"));
    }

}
