package io.github.danielcampossantos.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Plano {
    private Long id;
    private String descricao;
    private BigDecimal preco;
}
