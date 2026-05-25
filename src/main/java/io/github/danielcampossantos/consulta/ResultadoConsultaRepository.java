package io.github.danielcampossantos.consulta;

import io.github.danielcampossantos.conn.ConnectionFactory;
import io.github.danielcampossantos.consulta.dto.ExameResultado;
import io.github.danielcampossantos.consulta.dto.ReceitaResumo;
import io.github.danielcampossantos.consulta.dto.RemedioPrescrito;
import io.github.danielcampossantos.consulta.dto.ResultadoConsultaGetResponse;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.util.*;

public class ResultadoConsultaRepository {
    private static ResultadoConsultaRepository instance;

    public static ResultadoConsultaRepository getInstance() {
        if (instance == null) {
            instance = new ResultadoConsultaRepository();
        }
        return instance;
    }

    private ResultadoConsultaRepository() {
    }

    public Optional<ResultadoConsultaGetResponse> findByPacienteIdAndConsultaId(Long pacienteId, Long consultaId) {
        String sql = """
                SELECT
                    c.id AS consulta_id,
                    p.nome AS paciente_nome,
                    m.nome AS medico_nome,
                    e.descricao AS especialidade,
                    c.data_consulta,
                    c.horario,
                    d.diagnostico_descricao,
                    t.tratamento_descricao,
                    ex.descricao AS nome_exame,
                    rex.analise_resultados,
                    rec.id AS receituario_id,
                    rec.titulo_receituario,
                    rem.descricao AS nome_remedio,
                    rrem.descricao AS posologia
                FROM consulta c
                JOIN paciente p ON c.id_paciente = p.id
                JOIN medico m ON c.id_medico = m.id
                JOIN especialidade e ON m.id_especialidade = e.id
                LEFT JOIN diagnostico d ON c.id = d.id_consulta
                LEFT JOIN tratamento t ON d.id = t.id_diagnostico
                LEFT JOIN exames_solicitados es ON c.id = es.id_consulta
                LEFT JOIN exame ex ON es.id_exame = ex.id
                LEFT JOIN resultados_exames rex ON es.id_consulta = rex.id_consulta 
                    AND es.id_exame = rex.id_exame
                LEFT JOIN receituario rec ON c.id = rec.id_consulta
                LEFT JOIN receituario_remedios rrem ON rec.id = rrem.id_receituario
                LEFT JOIN remedio rem ON rrem.id_remedio = rem.id
                WHERE p.id = ? AND c.id = ?
                """;

        return executeSingleQuery(sql, pacienteId, consultaId);
    }

    public List<ResultadoConsultaGetResponse> findByPacienteId(Long pacienteId) {
        String sql = """
                SELECT
                    c.id AS consulta_id,
                    p.nome AS paciente_nome,
                    m.nome AS medico_nome,
                    e.descricao AS especialidade,
                    c.data_consulta,
                    c.horario,
                    d.diagnostico_descricao,
                    t.tratamento_descricao,
                    ex.descricao AS nome_exame,
                    rex.analise_resultados,
                    rec.id AS receituario_id,
                    rec.titulo_receituario,
                    rem.descricao AS nome_remedio,
                    rrem.descricao AS posologia
                FROM consulta c
                JOIN paciente p ON c.id_paciente = p.id
                JOIN medico m ON c.id_medico = m.id
                JOIN especialidade e ON m.id_especialidade = e.id
                LEFT JOIN diagnostico d ON c.id = d.id_consulta
                LEFT JOIN tratamento t ON d.id = t.id_diagnostico
                LEFT JOIN exames_solicitados es ON c.id = es.id_consulta
                LEFT JOIN exame ex ON es.id_exame = ex.id
                LEFT JOIN resultados_exames rex ON es.id_consulta = rex.id_consulta 
                    AND es.id_exame = rex.id_exame
                LEFT JOIN receituario rec ON c.id = rec.id_consulta
                LEFT JOIN receituario_remedios rrem ON rec.id = rrem.id_receituario
                LEFT JOIN remedio rem ON rrem.id_remedio = rem.id
                WHERE p.id = ?
                ORDER BY c.data_consulta DESC, c.horario
                """;

        return executeListQuery(sql, pacienteId);
    }

    private Optional<ResultadoConsultaGetResponse> executeSingleQuery(String sql, Long pacienteId, Long consultaId) {
        Map<Long, ResultadoConsultaGetResponse> resultadoMap = new LinkedHashMap<>();
        Map<Long, ReceitaResumo> receitaMap = new HashMap<>();

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, pacienteId);
            rowSet.setLong(2, consultaId);
            rowSet.execute();

            while (rowSet.next()) {
                buildResultado(rowSet, resultadoMap, receitaMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultadoMap.values().stream().findFirst();
    }

    private List<ResultadoConsultaGetResponse> executeListQuery(String sql, Long pacienteId) {
        Map<Long, ResultadoConsultaGetResponse> resultadoMap = new LinkedHashMap<>();
        Map<String, ReceitaResumo> receitaMap = new HashMap<>();

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, pacienteId);
            rowSet.execute();

            while (rowSet.next()) {
                buildResultado(rowSet, resultadoMap, receitaMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(resultadoMap.values());
    }

    private void buildResultado(JdbcRowSet rowSet, Map<Long, ResultadoConsultaGetResponse> resultadoMap,
                                Map<?, ReceitaResumo> receitaMap) throws SQLException {
        Long idConsulta = rowSet.getLong("consulta_id");
        String chaveReceita = idConsulta + "_" + rowSet.getLong("receituario_id");

        ResultadoConsultaGetResponse resultado = resultadoMap.computeIfAbsent(idConsulta, id -> {
            try {
                return ResultadoConsultaGetResponse.builder()
                        .idConsulta(id)
                        .pacienteNome(rowSet.getString("paciente_nome"))
                        .medicoNome(rowSet.getString("medico_nome"))
                        .especialidade(rowSet.getString("especialidade"))
                        .dataConsulta(rowSet.getDate("data_consulta") != null ?
                                rowSet.getDate("data_consulta").toLocalDate() : null)
                        .horario(rowSet.getTime("horario") != null ?
                                rowSet.getTime("horario").toLocalTime() : null)
                        .diagnostico(rowSet.getString("diagnostico_descricao"))
                        .tratamento(rowSet.getString("tratamento_descricao"))
                        .exames(new ArrayList<>())
                        .receitas(new ArrayList<>())
                        .build();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        String nomeExame = rowSet.getString("nome_exame");
        String analise = rowSet.getString("analise_resultados");
        if (nomeExame != null && resultado.exames().stream()
                .noneMatch(ex -> ex.nomeExame().equals(nomeExame))) {
            resultado.exames().add(ExameResultado.builder()
                    .nomeExame(nomeExame)
                    .resultado(analise != null ? analise : "Sem resultado")
                    .build());
        }

        Long receituarioId = rowSet.getLong("receituario_id");
        if (receituarioId != null && receituarioId > 0) {
            @SuppressWarnings("unchecked")
            Map<String, ReceitaResumo> receitaMapStr = (Map<String, ReceitaResumo>) receitaMap;

            ReceitaResumo receita = receitaMapStr.computeIfAbsent(chaveReceita, k -> {
                try {
                    ReceitaResumo nova = ReceitaResumo.builder()
                            .tituloReceita(rowSet.getString("titulo_receituario"))
                            .remedios(new ArrayList<>())
                            .build();
                    resultado.receitas().add(nova);
                    return nova;
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
    }
}