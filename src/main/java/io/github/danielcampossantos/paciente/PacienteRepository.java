package io.github.danielcampossantos.paciente;

import io.github.danielcampossantos.conn.ConnectionFactory;
import io.github.danielcampossantos.paciente.dto.PacienteGetResponse;
import io.github.danielcampossantos.paciente.dto.ProntuarioGetResponse;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.util.*;

public class PacienteRepository {
    private static PacienteRepository instance;

    public static PacienteRepository getInstance() {
        if (instance == null) {
            instance = new PacienteRepository();
        }
        return instance;
    }

    private PacienteRepository() {
    }


    public Optional<PacienteGetResponse> findById(Long id) {
        String sql = """
                SELECT
                    p.id,
                    p.nome,
                    p.cpf,
                    p.data_nascimento,
                    s.sobrenome,
                    pl.descricao AS plano_descricao,
                    pl.valor AS plano_valor,
                    r.descricao AS rua,
                    n.numero,
                    b.descricao AS bairro,
                    c.descricao AS cidade,
                    e.complemento
                FROM paciente p
                JOIN sobrenome s ON p.id_sobrenome = s.id
                LEFT JOIN plano pl ON p.id_plano = pl.id
                LEFT JOIN endereco e ON p.id_endereco = e.id
                LEFT JOIN rua r ON e.id_rua = r.id
                LEFT JOIN numero n ON e.id_numero = n.id
                LEFT JOIN bairro b ON e.id_bairro = b.id
                LEFT JOIN cidade c ON e.id_cidade = c.id
                WHERE p.id = ?
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, id);
            rowSet.execute();

            if (rowSet.next()) {
                var paciente = PacienteGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .nome(rowSet.getString("nome"))
                        .cpf(rowSet.getString("cpf"))
                        .dataNascimento(rowSet.getDate("data_nascimento").toLocalDate())
                        .sobrenome(rowSet.getString("sobrenome"))
                        .planoDescricao(rowSet.getString("plano_descricao"))
                        .planoValor(rowSet.getBigDecimal("plano_valor"))
                        .rua(rowSet.getString("rua"))
                        .numero(rowSet.getString("numero"))
                        .bairro(rowSet.getString("bairro"))
                        .cidade(rowSet.getString("cidade"))
                        .complemento(rowSet.getString("complemento"))
                        .build();

                return Optional.of(paciente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public List<PacienteGetResponse> findAll() {
        String sql = """
                SELECT
                    p.id,
                    p.nome,
                    p.cpf,
                    p.data_nascimento,
                    s.sobrenome,
                    pl.descricao AS plano_descricao,
                    pl.valor AS plano_valor,
                    r.descricao AS rua,
                    n.numero,
                    b.descricao AS bairro,
                    c.descricao AS cidade,
                    e.complemento
                FROM paciente p
                JOIN sobrenome s ON p.id_sobrenome = s.id
                LEFT JOIN plano pl ON p.id_plano = pl.id
                LEFT JOIN endereco e ON p.id_endereco = e.id
                LEFT JOIN rua r ON e.id_rua = r.id
                LEFT JOIN numero n ON e.id_numero = n.id
                LEFT JOIN bairro b ON e.id_bairro = b.id
                LEFT JOIN cidade c ON e.id_cidade = c.id
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.execute();
            List<PacienteGetResponse> pacientes = new ArrayList<>();

            while (rowSet.next()) {
                var paciente = PacienteGetResponse.builder()
                        .id(rowSet.getLong("id"))
                        .nome(rowSet.getString("nome"))
                        .cpf(rowSet.getString("cpf"))
                        .dataNascimento(rowSet.getDate("data_nascimento").toLocalDate())
                        .sobrenome(rowSet.getString("sobrenome"))
                        .planoDescricao(rowSet.getString("plano_descricao"))
                        .planoValor(rowSet.getBigDecimal("plano_valor"))
                        .rua(rowSet.getString("rua"))
                        .numero(rowSet.getString("numero"))
                        .bairro(rowSet.getString("bairro"))
                        .cidade(rowSet.getString("cidade"))
                        .complemento(rowSet.getString("complemento"))
                        .build();
                pacientes.add(paciente);
            }
            return pacientes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<ProntuarioGetResponse> findAllProntuario() {
        String sql = """
                SELECT DISTINCT
                    p.id AS paciente_id,
                    p.nome AS paciente_nome,
                    m.nome AS medico_nome,
                    ex.descricao AS exame,
                    rex.analise_resultados,
                    d.diagnostico_descricao,
                    t.tratamento_descricao
                FROM paciente p
                LEFT JOIN consulta cons ON p.id = cons.id_paciente
                LEFT JOIN medico m ON cons.id_medico = m.id
                LEFT JOIN exames_solicitados es ON cons.id = es.id_consulta
                LEFT JOIN exame ex ON es.id_exame = ex.id
                LEFT JOIN resultados_exames rex ON es.id_consulta = rex.id_consulta 
                    AND es.id_exame = rex.id_exame
                LEFT JOIN diagnostico d ON cons.id = d.id_consulta
                LEFT JOIN tratamento t ON d.id = t.id_diagnostico
                ORDER BY p.id
                """;

        Map<Long, ProntuarioGetResponse> prontuarioMap = new LinkedHashMap<>();

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.execute();

            while (rowSet.next()) {
                Long pacienteId = rowSet.getLong("paciente_id");

                ProntuarioGetResponse prontuario = prontuarioMap.computeIfAbsent(pacienteId, id -> {
                    try {
                        return ProntuarioGetResponse.builder()
                                .id(id)
                                .nomePaciente(rowSet.getString("paciente_nome"))
                                .nomeMedico(rowSet.getString("medico_nome"))
                                .examesComResultados(new HashMap<>())
                                .diagnosticos(new ArrayList<>())
                                .tratamentos(new ArrayList<>())
                                .build();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                String exame = rowSet.getString("exame");
                String resultado = rowSet.getString("analise_resultados");
                if (exame != null) {
                    prontuario.examesComResultados()
                            .computeIfAbsent(exame, k -> new ArrayList<>())
                            .add(resultado != null ? resultado : "Sem resultado");
                }

                String diagnostico = rowSet.getString("diagnostico_descricao");
                if (diagnostico != null && !prontuario.diagnosticos().contains(diagnostico)) {
                    prontuario.diagnosticos().add(diagnostico);
                }

                String tratamento = rowSet.getString("tratamento_descricao");
                if (tratamento != null && !prontuario.tratamentos().contains(tratamento)) {
                    prontuario.tratamentos().add(tratamento);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(prontuarioMap.values());
    }

    public Optional<ProntuarioGetResponse> findByIdProntuario(Long id) {
        String sql = """
                SELECT DISTINCT
                    p.id AS paciente_id,
                    p.nome AS paciente_nome,
                    m.nome AS medico_nome,
                    ex.descricao AS exame,
                    rex.analise_resultados,
                    d.diagnostico_descricao,
                    t.tratamento_descricao
                FROM paciente p
                LEFT JOIN consulta cons ON p.id = cons.id_paciente
                LEFT JOIN medico m ON cons.id_medico = m.id
                LEFT JOIN exames_solicitados es ON cons.id = es.id_consulta
                LEFT JOIN exame ex ON es.id_exame = ex.id
                LEFT JOIN resultados_exames rex ON es.id_consulta = rex.id_consulta 
                    AND es.id_exame = rex.id_exame
                LEFT JOIN diagnostico d ON cons.id = d.id_consulta
                LEFT JOIN tratamento t ON d.id = t.id_diagnostico
                WHERE p.id = ?
                """;

        ProntuarioGetResponse prontuario = null;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, id);
            rowSet.execute();

            while (rowSet.next()) {
                if (prontuario == null) {
                    prontuario = ProntuarioGetResponse.builder()
                            .id(rowSet.getLong("paciente_id"))
                            .nomePaciente(rowSet.getString("paciente_nome"))
                            .nomeMedico(rowSet.getString("medico_nome"))
                            .examesComResultados(new HashMap<>())
                            .diagnosticos(new ArrayList<>())
                            .tratamentos(new ArrayList<>())
                            .build();
                }

                String exame = rowSet.getString("exame");
                String resultado = rowSet.getString("analise_resultados");
                if (exame != null) {
                    prontuario.examesComResultados()
                            .computeIfAbsent(exame, k -> new ArrayList<>())
                            .add(resultado != null ? resultado : "Sem resultado");
                }

                String diagnostico = rowSet.getString("diagnostico_descricao");
                if (diagnostico != null && !prontuario.diagnosticos().contains(diagnostico)) {
                    prontuario.diagnosticos().add(diagnostico);
                }

                String tratamento = rowSet.getString("tratamento_descricao");
                if (tratamento != null && !prontuario.tratamentos().contains(tratamento)) {
                    prontuario.tratamentos().add(tratamento);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(prontuario);
    }

}
