package io.github.danielcampossantos.agendamento.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record AgendamentoGetResponse(
        Long id,
        String pacienteNome,
        String medicoNome,
        String especialidade,
        String atendenteNome,
        BigDecimal valor,
        LocalDate dataAgendamento,
        LocalDate dataConsulta,
        LocalTime horario
) {}