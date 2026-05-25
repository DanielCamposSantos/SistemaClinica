package io.github.danielcampossantos.receita.dto;

import io.github.danielcampossantos.consulta.dto.RemedioPrescrito;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReceitaResponse(
        Long idReceita,
        String pacienteNome,
        String medicoNome,
        String crmMedico,
        String tituloReceita,
        LocalDateTime dataHorario,
        List<RemedioPrescrito> remedios
) {

}