package io.github.danielcampossantos.medico.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MedicoGetResponse(
        Long id,
        String nome,
        String sobrenome,
        String crm,
        String especialidade,
        BigDecimal salario
) {
}