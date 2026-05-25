package io.github.danielcampossantos.atendente;

import io.github.danielcampossantos.atendente.dto.AtendenteGetResponse;
import io.github.danielcampossantos.conn.ConnectionFactory;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AtendenteRepository {
    private static AtendenteRepository instance;

    public static AtendenteRepository getInstance() {
        if (instance == null) {
            instance = new AtendenteRepository();
        }
        return instance;
    }

    private AtendenteRepository() {
    }

    public Optional<AtendenteGetResponse> findById(Long id) {
        String sql = """
                SELECT
                    a.id,
                    a.nome,
                    s.sobrenome,
                    a.email,
                    a.salario,
                    a.data_nascimento
                FROM atendente a
                JOIN sobrenome s ON a.id_sobrenome = s.id
                WHERE a.id = ?
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, id);
            rowSet.execute();

            if (rowSet.next()) {
                var atendente = AtendenteGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .nome(rowSet.getString("nome"))
                        .sobrenome(rowSet.getString("sobrenome"))
                        .email(rowSet.getString("email"))
                        .salario(rowSet.getBigDecimal("salario"))
                        .dataNascimento(rowSet.getDate("data_nascimento") != null ?
                                rowSet.getDate("data_nascimento").toLocalDate() : null)
                        .build();

                return Optional.of(atendente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<AtendenteGetResponse> findAll() {
        String sql = """
                SELECT
                    a.id,
                    a.nome,
                    s.sobrenome,
                    a.email,
                    a.salario,
                    a.data_nascimento
                FROM atendente a
                JOIN sobrenome s ON a.id_sobrenome = s.id
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.execute();
            List<AtendenteGetResponse> atendentes = new ArrayList<>();

            while (rowSet.next()) {
                var atendente = AtendenteGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .nome(rowSet.getString("nome"))
                        .sobrenome(rowSet.getString("sobrenome"))
                        .email(rowSet.getString("email"))
                        .salario(rowSet.getBigDecimal("salario"))
                        .dataNascimento(rowSet.getDate("data_nascimento") != null ?
                                rowSet.getDate("data_nascimento").toLocalDate() : null)
                        .build();
                atendentes.add(atendente);
            }
            return atendentes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}