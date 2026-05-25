package io.github.danielcampossantos.consulta.dto;

import lombok.Builder;

@Builder
public record ExameResultado(
        String nomeExame,
        String resultado
) {}