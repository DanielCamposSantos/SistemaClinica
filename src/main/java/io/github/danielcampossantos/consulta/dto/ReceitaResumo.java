package io.github.danielcampossantos.consulta.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ReceitaResumo(
        String tituloReceita,
        List<RemedioPrescrito> remedios
) {}