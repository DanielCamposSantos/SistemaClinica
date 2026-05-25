package io.github.danielcampossantos.medico;

import io.github.danielcampossantos.conn.ConnectionFactory;
import io.github.danielcampossantos.medico.dto.MedicoGetResponse;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MedicoRepository {
    private static MedicoRepository instance;

    public static MedicoRepository getInstance() {
        if (instance == null) {
            instance = new MedicoRepository();
        }
        return instance;
    }

    private MedicoRepository() {
    }

    public Optional<MedicoGetResponse> findById(Long id) {
        String sql = """
                SELECT
                    m.id,
                    m.nome,
                    s.sobrenome,
                    m.crm,
                    e.descricao AS especialidade,
                    m.salario
                FROM medico m
                JOIN sobrenome s ON m.id_sobrenome = s.id
                JOIN especialidade e ON m.id_especialidade = e.id
                WHERE m.id = ?
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, id);
            rowSet.execute();

            if (rowSet.next()) {
                var medico = MedicoGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .nome(rowSet.getString("nome"))
                        .sobrenome(rowSet.getString("sobrenome"))
                        .crm(rowSet.getString("crm"))
                        .especialidade(rowSet.getString("especialidade"))
                        .salario(rowSet.getBigDecimal("salario"))
                        .build();

                return Optional.of(medico);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<MedicoGetResponse> findAll() {
        String sql = """
                SELECT
                    m.id,
                    m.nome,
                    s.sobrenome,
                    m.crm,
                    e.descricao AS especialidade,
                    m.salario
                FROM medico m
                JOIN sobrenome s ON m.id_sobrenome = s.id
                JOIN especialidade e ON m.id_especialidade = e.id
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.execute();
            List<MedicoGetResponse> medicos = new ArrayList<>();

            while (rowSet.next()) {
                var medico = MedicoGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .nome(rowSet.getString("nome"))
                        .sobrenome(rowSet.getString("sobrenome"))
                        .crm(rowSet.getString("crm"))
                        .especialidade(rowSet.getString("especialidade"))
                        .salario(rowSet.getBigDecimal("salario"))
                        .build();
                medicos.add(medico);
            }
            return medicos;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}