package io.github.danielcampossantos.atendente.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AtendenteGetResponse(
        Long id,
        String nome,
        String sobrenome,
        String email,
        BigDecimal salario,
        LocalDate dataNascimento
) {
}