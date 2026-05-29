package io.github.danielcampossantos.consulta.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
public record ResultadoConsultaGetResponse(
        Long idConsulta,
        String pacienteNome,
        String medicoNome,
        String especialidade,
        LocalDate dataConsulta,
        LocalTime horario,
        String diagnostico,
        String tratamento,
        List<ExameResultado> exames,
        List<ReceitaResumo> receitas
) {
}
