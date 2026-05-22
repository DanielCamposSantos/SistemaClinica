package io.github.danielcampossantos.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class Paciente {
    private Long id;
    private String nome;
    private String cpf;
    private String endereco;
    private LocalDate dataNascimento;
    private Plano plano;


}
