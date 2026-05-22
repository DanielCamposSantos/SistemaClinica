package io.github.danielcampossantos.dbSimulator;

import io.github.danielcampossantos.domain.Paciente;
import io.github.danielcampossantos.domain.Plano;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PacienteData {
    private final List<Paciente> pacientes = new ArrayList<>(List.of(
            Paciente.builder()
                    .id(1L)
                    .nome("João Silva")
                    .cpf("123.456.789-00")
                    .endereco("Rua das Flores, 123 - São Paulo/SP")
                    .dataNascimento(LocalDate.of(1990, 5, 15))
                    .plano(Plano.builder()
                            .descricao("Plano Básico")
                            .preco(new BigDecimal("150.00"))
                            .build())
                    .build(),

            Paciente.builder()
                    .id(2L)
                    .nome("Maria Oliveira")
                    .cpf("987.654.321-00")
                    .endereco("Av. Paulista, 1000 - São Paulo/SP")
                    .dataNascimento(LocalDate.of(1985, 8, 22))
                    .plano(Plano.builder()
                            .descricao("Plano Premium")
                            .preco(new BigDecimal("350.00"))
                            .build())
                    .build(),

            Paciente.builder()
                    .id(3L)
                    .nome("Carlos Santos")
                    .cpf("456.789.123-00")
                    .endereco("Rua da Praia, 45 - Rio de Janeiro/RJ")
                    .dataNascimento(LocalDate.of(2000, 12, 10))
                    .plano(Plano.builder()
                            .descricao("Plano Básico")
                            .preco(new BigDecimal("150.00"))
                            .build())
                    .build(),

            Paciente.builder()
                    .id(4L)
                    .nome("Ana Costa")
                    .cpf("789.123.456-00")
                    .endereco("Rua das Palmeiras, 78 - Belo Horizonte/MG")
                    .dataNascimento(LocalDate.of(1995, 3, 5))
                    .plano(Plano.builder()
                            .descricao("Plano Familiar")
                            .preco(new BigDecimal("280.00"))
                            .build())
                    .build(),

            Paciente.builder()
                    .id(5L)
                    .nome("Pedro Rodrigues")
                    .cpf("321.654.987-00")
                    .endereco("Av. Brasil, 500 - Curitiba/PR")
                    .dataNascimento(LocalDate.of(1988, 11, 30))
                    .plano(Plano.builder()
                            .descricao("Plano Premium")
                            .preco(new BigDecimal("350.00"))
                            .build())
                    .build(),

            Paciente.builder()
                    .id(6L)
                    .nome("Fernanda Lima")
                    .cpf("159.753.486-00")
                    .endereco("Rua das Acácias, 200 - Porto Alegre/RS")
                    .dataNascimento(LocalDate.of(1992, 7, 18))
                    .plano(Plano.builder()
                            .descricao("Plano Básico")
                            .preco(new BigDecimal("150.00"))
                            .build())
                    .build()
    ));

    public List<Paciente> getPacientes() {
        return pacientes;
    }
}