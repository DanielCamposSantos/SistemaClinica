package io.github.danielcampossantos.consulta;

import io.github.danielcampossantos.consulta.dto.ResultadoConsultaGetResponse;
import io.github.danielcampossantos.exception.BadRequestException;

import java.util.List;

public class ResultadoService {
    private static ResultadoService instance;
    private final ResultadoConsultaRepository repository;

    private ResultadoService() {
        repository = ResultadoConsultaRepository.getInstance();
    }

    public static ResultadoService getInstance() {
        if (instance == null) {
            instance = new ResultadoService();
        }
        return instance;
    }

    public ResultadoConsultaGetResponse findByPacienteIdAndConsultaId(Long pacienteId, Long consultaId) {
        return repository.findByPacienteIdAndConsultaId(pacienteId, consultaId)
                .orElseThrow(() -> new BadRequestException("Resultado não encontrado"));
    }

    public List<ResultadoConsultaGetResponse> findByPacienteId(Long pacienteId) {
        List<ResultadoConsultaGetResponse> resultados = repository.findByPacienteId(pacienteId);
        if (resultados.isEmpty()) {
            throw new BadRequestException("Nenhum resultado encontrado para este paciente");
        }
        return resultados;
    }
}