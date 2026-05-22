package io.github.danielcampossantos.paciente;

import io.github.danielcampossantos.conn.ConnectionFactory;
import io.github.danielcampossantos.conn.Repository;
import io.github.danielcampossantos.domain.Paciente;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PacienteRepository implements Repository<Paciente> {
    private static PacienteRepository instance;

    public static PacienteRepository getInstance() {
        if (instance == null) {
            instance = new PacienteRepository();
        }
        return instance;
    }

    private PacienteRepository() {
    }


    @Override
    public Optional<Paciente> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Paciente> findAll() {
        String sql = "SELECT * FROM paciente";
        List<Paciente> pacientes = new ArrayList<>();
        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet();
        ) {
            rowSet.setCommand(sql);
            rowSet.execute();
            while (rowSet.next()) {
                var paciente = Paciente.builder()
                        .id(rowSet.getLong("id"))
                        .nome(rowSet.getString("nome"))
                        .cpf(rowSet.getString("cpf"))
                        .endereco(rowSet.getString("endereco"))
                        .dataNascimento(rowSet.getDate("data_nascimento").toLocalDate())
                        .idPlano(rowSet.getLong("id_plano"))
                        .build();

                pacientes.add(paciente);
            }

            return pacientes;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public Paciente save(Paciente entity) {
        return null;
    }
}
