package io.github.danielcampossantos.repository;

import io.github.danielcampossantos.domain.Paciente;

import java.util.List;


public class PacienteHardCodedRepository {
    private static PacienteHardCodedRepository instance;

    private final PacienteData pacienteData;

    private PacienteHardCodedRepository() {
        this.pacienteData = new PacienteData();
    }

    public static PacienteHardCodedRepository getInstance() {
        if (instance == null) {
            instance = new PacienteHardCodedRepository();
        }
        return instance;
    }


    public PacienteHardCodedRepository(PacienteData pacienteData) {
        this.pacienteData = pacienteData;
    }

    public List<Paciente> findAll() {
        return pacienteData.getPacientes();
    }

}