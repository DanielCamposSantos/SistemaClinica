package io.github.danielcampossantos.agendamento;

import io.github.danielcampossantos.conn.ConnectionFactory;
import io.github.danielcampossantos.agendamento.dto.AgendamentoGetResponse;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.util.*;

public class AgendamentoRepository {
    private static AgendamentoRepository instance;

    public static AgendamentoRepository getInstance() {
        if (instance == null) {
            instance = new AgendamentoRepository();
        }
        return instance;
    }

    private AgendamentoRepository() {
    }

    public Optional<AgendamentoGetResponse> findById(Long id) {
        String sql = """
                SELECT
                    c.id,
                    p.nome AS paciente_nome,
                    m.nome AS medico_nome,
                    e.descricao AS especialidade,
                    a.nome AS atendente_nome,
                    c.valor,
                    c.data_agendamento,
                    c.data_consulta,
                    c.horario
                FROM consulta c
                JOIN paciente p ON c.id_paciente = p.id
                JOIN medico m ON c.id_medico = m.id
                JOIN especialidade e ON m.id_especialidade = e.id
                JOIN atendente a ON c.id_atendente = a.id
                WHERE c.id = ?
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, id);
            rowSet.execute();

            if (rowSet.next()) {
                var agendamento = AgendamentoGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .pacienteNome(rowSet.getString("paciente_nome"))
                        .medicoNome(rowSet.getString("medico_nome"))
                        .especialidade(rowSet.getString("especialidade"))
                        .atendenteNome(rowSet.getString("atendente_nome"))
                        .valor(rowSet.getBigDecimal("valor"))
                        .dataAgendamento(rowSet.getDate("data_agendamento") != null ?
                                rowSet.getDate("data_agendamento").toLocalDate() : null)
                        .dataConsulta(rowSet.getDate("data_consulta") != null ?
                                rowSet.getDate("data_consulta").toLocalDate() : null)
                        .horario(rowSet.getTime("horario") != null ?
                                rowSet.getTime("horario").toLocalTime() : null)
                        .build();

                return Optional.of(agendamento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<AgendamentoGetResponse> findAll() {
        String sql = """
                SELECT
                    c.id,
                    p.nome AS paciente_nome,
                    m.nome AS medico_nome,
                    e.descricao AS especialidade,
                    a.nome AS atendente_nome,
                    c.valor,
                    c.data_agendamento,
                    c.data_consulta,
                    c.horario
                FROM consulta c
                JOIN paciente p ON c.id_paciente = p.id
                JOIN medico m ON c.id_medico = m.id
                JOIN especialidade e ON m.id_especialidade = e.id
                JOIN atendente a ON c.id_atendente = a.id
                ORDER BY c.data_consulta DESC, c.horario
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.execute();
            List<AgendamentoGetResponse> agendamentos = new ArrayList<>();

            while (rowSet.next()) {
                var agendamento = AgendamentoGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .pacienteNome(rowSet.getString("paciente_nome"))
                        .medicoNome(rowSet.getString("medico_nome"))
                        .especialidade(rowSet.getString("especialidade"))
                        .atendenteNome(rowSet.getString("atendente_nome"))
                        .valor(rowSet.getBigDecimal("valor"))
                        .dataAgendamento(rowSet.getDate("data_agendamento") != null ?
                                rowSet.getDate("data_agendamento").toLocalDate() : null)
                        .dataConsulta(rowSet.getDate("data_consulta") != null ?
                                rowSet.getDate("data_consulta").toLocalDate() : null)
                        .horario(rowSet.getTime("horario") != null ?
                                rowSet.getTime("horario").toLocalTime() : null)
                        .build();
                agendamentos.add(agendamento);
            }
            return agendamentos;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<AgendamentoGetResponse> findByPacienteId(Long pacienteId) {
        String sql = """
                SELECT
                    c.id,
                    p.nome AS paciente_nome,
                    m.nome AS medico_nome,
                    e.descricao AS especialidade,
                    a.nome AS atendente_nome,
                    c.valor,
                    c.data_agendamento,
                    c.data_consulta,
                    c.horario
                FROM consulta c
                JOIN paciente p ON c.id_paciente = p.id
                JOIN medico m ON c.id_medico = m.id
                JOIN especialidade e ON m.id_especialidade = e.id
                JOIN atendente a ON c.id_atendente = a.id
                WHERE p.id = ?
                ORDER BY c.data_consulta DESC, c.horario
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, pacienteId);
            rowSet.execute();
            List<AgendamentoGetResponse> agendamentos = new ArrayList<>();

            while (rowSet.next()) {
                var agendamento = AgendamentoGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .pacienteNome(rowSet.getString("paciente_nome"))
                        .medicoNome(rowSet.getString("medico_nome"))
                        .especialidade(rowSet.getString("especialidade"))
                        .atendenteNome(rowSet.getString("atendente_nome"))
                        .valor(rowSet.getBigDecimal("valor"))
                        .dataAgendamento(rowSet.getDate("data_agendamento") != null ?
                                rowSet.getDate("data_agendamento").toLocalDate() : null)
                        .dataConsulta(rowSet.getDate("data_consulta") != null ?
                                rowSet.getDate("data_consulta").toLocalDate() : null)
                        .horario(rowSet.getTime("horario") != null ?
                                rowSet.getTime("horario").toLocalTime() : null)
                        .build();
                agendamentos.add(agendamento);
            }
            return agendamentos;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<AgendamentoGetResponse> findByMes(int mes, int ano) {
        String sql = """
            SELECT
                c.id,
                p.nome AS paciente_nome,
                m.nome AS medico_nome,
                e.descricao AS especialidade,
                a.nome AS atendente_nome,
                c.valor,
                c.data_agendamento,
                c.data_consulta,
                c.horario
            FROM consulta c
            JOIN paciente p ON c.id_paciente = p.id
            JOIN medico m ON c.id_medico = m.id
            JOIN especialidade e ON m.id_especialidade = e.id
            JOIN atendente a ON c.id_atendente = a.id
            WHERE EXTRACT(MONTH FROM c.data_consulta) = ? 
            AND EXTRACT(YEAR FROM c.data_consulta) = ?
            ORDER BY c.data_consulta, c.horario
            """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setInt(1, mes);
            rowSet.setInt(2, ano);
            rowSet.execute();
            List<AgendamentoGetResponse> agendamentos = new ArrayList<>();

            while (rowSet.next()) {
                var agendamento = AgendamentoGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .pacienteNome(rowSet.getString("paciente_nome"))
                        .medicoNome(rowSet.getString("medico_nome"))
                        .especialidade(rowSet.getString("especialidade"))
                        .atendenteNome(rowSet.getString("atendente_nome"))
                        .valor(rowSet.getBigDecimal("valor"))
                        .dataAgendamento(rowSet.getDate("data_agendamento") != null ?
                                rowSet.getDate("data_agendamento").toLocalDate() : null)
                        .dataConsulta(rowSet.getDate("data_consulta") != null ?
                                rowSet.getDate("data_consulta").toLocalDate() : null)
                        .horario(rowSet.getTime("horario") != null ?
                                rowSet.getTime("horario").toLocalTime() : null)
                        .build();
                agendamentos.add(agendamento);
            }
            return agendamentos;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}