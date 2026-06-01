package io.github.danielcampossantos.paciente.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PacienteGetResponse(
        Long id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String sobrenome,
        String planoDescricao,
        BigDecimal planoValor,
        String rua,
        String numero,
        String bairro,
        String cidade,
        String complemento,
        String ddd,
        String telefone
) {
}