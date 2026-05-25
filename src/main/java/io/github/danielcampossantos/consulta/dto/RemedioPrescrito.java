package io.github.danielcampossantos.consulta.dto;

import lombok.Builder;

@Builder
public record RemedioPrescrito(
        String nomeRemedio,
        String posologia
) {}