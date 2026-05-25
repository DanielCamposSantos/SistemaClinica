package io.github.danielcampossantos.receita;

import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.receita.dto.ReceitaResponse;

import java.util.List;

public class ReceitaService {
    private static ReceitaService instance;
    private final ReceitaRepository repository;

    private ReceitaService() {
        repository = ReceitaRepository.getInstance();
    }

    public static ReceitaService getInstance() {
        if (instance == null) {
            instance = new ReceitaService();
        }
        return instance;
    }

    public List<ReceitaResponse> findByPacienteId(Long pacienteId) {
        List<ReceitaResponse> receitas = repository.findByPacienteId(pacienteId);
        if (receitas.isEmpty()) {
            throw new BadRequestException("Nenhuma receita encontrada para este paciente");
        }
        return receitas;
    }
}