package io.github.danielcampossantos.paciente.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ProntuarioGetResponse(
        Long id,
        String nomePaciente,
        String nomeMedico,
        Map<String, List<String>> examesComResultados,
        List<String> diagnosticos,
        List<String> tratamentos
) {
}