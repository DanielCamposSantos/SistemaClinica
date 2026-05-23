package io.github.danielcampossantos.paciente;

import io.github.danielcampossantos.conn.ConnectionFactory;
import io.github.danielcampossantos.conn.Repository;
import io.github.danielcampossantos.domain.Paciente;
import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.paciente.dto.Prontuario;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.util.*;

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
        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()
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


    public Optional<Prontuario> getProntuarioByPacienteId(Long id) {
        String sql = """
                SELECT
                    p.nome as paciente_nome,
                    m.nome as medico_nome,
                    ex.descricao as exame_descricao,
                    re.analise_resultados,
                    d.diagnostico_descricao,
                    t.tratamento_descricao
                FROM consulta c
                INNER JOIN paciente p ON p.id = c.id_paciente
                INNER JOIN medico m ON m.id = c.id_medico
                INNER JOIN exames_solicitados es ON es.id_consulta = c.id
                INNER JOIN exame ex ON ex.id = es.id_exame
                INNER JOIN resultados_exames re ON re.id_exames_solicitados = es.id
                INNER JOIN diagnostico d ON d.id_consulta = c.id
                INNER JOIN tratamento t ON t.id_diagnostico = d.id
                WHERE p.id = ?
                ORDER BY p.nome ASC;
                """;

        try (JdbcRowSet rowSet = ConnectionFactory.getJdbcRowSet()) {
            rowSet.setCommand(sql);
            rowSet.setLong(1, id);
            rowSet.execute();

            Map<String, List<String>> examesComResultados = new LinkedHashMap<>();
            Set<String> diagnosticosSet = new LinkedHashSet<>();
            Set<String> tratamentosSet = new LinkedHashSet<>();

            String nomePaciente = null;
            String nomeMedico = null;

            if (!rowSet.next()) {
                throw new BadRequestException("Nenhum valor foi encontrado");
            }

            while (rowSet.next()) {
                if (nomePaciente == null) {
                    nomePaciente = rowSet.getString("paciente_nome");
                }
                if (nomeMedico == null) {
                    nomeMedico = rowSet.getString("medico_nome");
                }

                String exame = rowSet.getString("exame_descricao");
                String resultado = rowSet.getString("analise_resultados");

                if (exame != null && resultado != null) {
                    examesComResultados.computeIfAbsent(exame, k -> new ArrayList<>())
                            .add(resultado);
                }

                String diagnostico = rowSet.getString("diagnostico_descricao");
                if (diagnostico != null && !diagnostico.trim().isEmpty()) {
                    diagnosticosSet.add(diagnostico);
                }

                String tratamento = rowSet.getString("tratamento_descricao");
                if (tratamento != null && !tratamento.trim().isEmpty()) {
                    tratamentosSet.add(tratamento);
                }
            }

            examesComResultados.forEach((exame, resultados) -> {
                List<String> semDuplicatas = new ArrayList<>(new LinkedHashSet<>(resultados));
                examesComResultados.put(exame, semDuplicatas);
            });

            Prontuario prontuario = Prontuario.builder()
                    .nomePaciente(nomePaciente)
                    .nomeMedico(nomeMedico)
                    .examesComResultados(examesComResultados)
                    .diagnosticos(new ArrayList<>(diagnosticosSet))
                    .tratamentos(new ArrayList<>(tratamentosSet))
                    .build();

            return Optional.of(prontuario);

        } catch (SQLException e) {
            return Optional.empty();
        }
    }


}
