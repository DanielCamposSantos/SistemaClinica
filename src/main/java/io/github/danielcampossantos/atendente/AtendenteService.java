package io.github.danielcampossantos.atendente;


import io.github.danielcampossantos.atendente.dto.AtendenteGetResponse;
import io.github.danielcampossantos.exception.BadRequestException;

import java.util.List;

public class AtendenteService {
    private static AtendenteService instance;

    private final AtendenteRepository repository;

    private AtendenteService() {
        repository = AtendenteRepository.getInstance();
    }

    public static AtendenteService getInstance() {
        if (instance == null) {
            instance = new AtendenteService();
        }
        return instance;
    }

    public List<AtendenteGetResponse> findAll() {
        return repository.findAll();
    }

    public AtendenteGetResponse findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Atendente não encontrado"));
    }

}
