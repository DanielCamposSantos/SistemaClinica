package io.github.danielcampossantos.receita;


import io.github.danielcampossantos.conn.ConnectionFactory;
import io.github.danielcampossantos.consulta.dto.RemedioPrescrito;
import io.github.danielcampossantos.receita.dto.ReceitaResponse;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.util.*;

public class ReceitaRepository {
    private static ReceitaRepository instance;

    public static ReceitaRepository getInstance() {
        if (instance == null) {
            instance = new ReceitaRepository();
        }
        return instance;
    }

    private ReceitaRepository() {
    }

    public List<ReceitaResponse> findByPacienteId(Long pacienteId) {
        String sql = """
                SELECT
                    rec.id AS receita_id,
                    p.nome AS paciente_nome,
                    m.nome AS medico_nome,
                    m.crm,
                    rec.titulo_receituario,
                    rec.data_horario,
                    rem.descricao AS nome_remedio,
                    rrem.descricao AS posologia
                FROM receituario rec
                JOIN consulta c ON rec.id_consulta = c.id
                JOIN paciente p ON c.id_paciente = p.id
                JOIN medico m ON c.id_medico = m.id
                LEFT JOIN receituario_remedios rrem ON rec.id = rrem.id_receituario
                LEFT JOIN remedio rem ON rrem.id_remedio = rem.id
                WHERE p.id = ?
                ORDER BY rec.data_horario DESC
                """;

        Map<Long, ReceitaResponse> receitaMap = new LinkedHashMap<>();

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, pacienteId);
            rowSet.execute();

            while (rowSet.next()) {
                Long receitaId = rowSet.getLong("receita_id");

                ReceitaResponse receita = receitaMap.computeIfAbsent(receitaId, id -> {
                    try {
                        return ReceitaResponse.builder()
                                .idReceita(id)
                                .pacienteNome(rowSet.getString("paciente_nome"))
                                .medicoNome(rowSet.getString("medico_nome"))
                                .crmMedico(rowSet.getString("crm"))
                                .tituloReceita(rowSet.getString("titulo_receituario"))
                                .dataHorario(rowSet.getTimestamp("data_horario") != null ?
                                        rowSet.getTimestamp("data_horario").toLocalDateTime() : null)
                                .remedios(new ArrayList<>())
                                .build();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                String nomeRemedio = rowSet.getString("nome_remedio");
                if (nomeRemedio != null) {
                    receita.remedios().add(RemedioPrescrito.builder()
                            .nomeRemedio(nomeRemedio)
                            .posologia(rowSet.getString("posologia") != null ?
                                    rowSet.getString("posologia") : "Conforme prescrição médica")
                            .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(receitaMap.values());
    }
}