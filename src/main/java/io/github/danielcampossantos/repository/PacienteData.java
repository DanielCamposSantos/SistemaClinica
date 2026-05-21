package io.github.danielcampossantos.repository;

import io.github.danielcampossantos.domain.Paciente;

import java.util.ArrayList;
import java.util.List;

public class PacienteData {
    private final List<Paciente> pacientes = new ArrayList<>(List.of(
            Paciente.builder()
                    .id(1L)
                    .nome("Ana Clara Souza")
                    .cpf("123.456.789-01")
                    .rg("MG-12.345.678")
                    .telefone("(31) 98765-4321")
                    .email("ana.souza@email.com")
                    .build(),
            Paciente.builder()
                    .id(2L)
                    .nome("Bruno Martins Oliveira")
                    .cpf("234.567.890-12")
                    .rg("SP-23.456.789")
                    .telefone("(11) 97654-3210")
                    .email("bruno.oliveira@email.com")
                    .build(),
            Paciente.builder()
                    .id(3L)
                    .nome("Carla Fernanda Lima")
                    .cpf("345.678.901-23")
                    .rg("RJ-34.567.890")
                    .telefone("(21) 96543-2109")
                    .email("carla.lima@email.com")
                    .build(),
            Paciente.builder()
                    .id(4L)
                    .nome("Diego Ferreira Santos")
                    .cpf("456.789.012-34")
                    .rg("MG-45.678.901")
                    .telefone("(31) 95432-1098")
                    .email("diego.santos@email.com")
                    .build(),
            Paciente.builder()
                    .id(5L)
                    .nome("Elena Rodrigues Costa")
                    .cpf("567.890.123-45")
                    .rg("SP-56.789.012")
                    .telefone("(11) 94321-0987")
                    .email("elena.costa@email.com")
                    .build()
    ));

    public List<Paciente> getPacientes() {
        return pacientes;
    }
}
