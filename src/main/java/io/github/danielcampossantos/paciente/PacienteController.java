package io.github.danielcampossantos.paciente;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.danielcampossantos.consulta.ResultadoService;
import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.receita.ReceitaService;
import io.github.danielcampossantos.utils.HttpUtils;

import java.io.IOException;

public class PacienteController implements HttpHandler {

    private static PacienteController instance;

    private final PacienteService pacienteService;
    private final ResultadoService resultadoService;
    private final ReceitaService receitaService;
    private final Gson gson;

    private PacienteController() {
        pacienteService = PacienteService.getInstance();
        resultadoService = ResultadoService.getInstance();
        receitaService = ReceitaService.getInstance();
        gson = new Gson();
    }

    public static PacienteController getInstance() {
        if (instance == null) {
            instance = new PacienteController();
        }
        return instance;
    }

    private static final String CONTENT_TYPE = "application/json";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtils.setMethods(exchange,this::handleGet);

    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        try {
            //GET /pacientes/{id} - Buscar paciente por ID
            if (path.matches("/pacientes/\\d+")) {
                String[] parts = path.split("/");
                var id = Long.parseLong(parts[parts.length - 1]);
                var paciente = pacienteService.findById(id);
                String json = gson.toJson(paciente);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            //GET /pacientes - Listar todos os pacientes
            if (path.equals("/pacientes")) {
                var listaPacientes = pacienteService.findAll();
                String json = gson.toJson(listaPacientes);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }


            //GET /pacientes/prontuarios/{id} - Buscar prontuário específico (MAIS ESPECÍFICO PRIMEIRO)
            if (path.matches("/pacientes/prontuarios/\\d+")) {
                String[] parts = path.split("/");
                var id = Long.parseLong(parts[parts.length - 1]);
                var prontuario = pacienteService.findProntuarioByPacienteId(id);
                String json = gson.toJson(prontuario);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            //GET /pacientes/prontuarios - Listar todos os prontuários
            if (path.equals("/pacientes/prontuarios")) {
                var listaProntuarios = pacienteService.findAllProntuarios();
                String json = gson.toJson(listaProntuarios);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            // GET /pacientes/{id}/resultados/{consultaId}
            if (path.matches("/pacientes/\\d+/resultados/\\d+")) {
                String[] parts = path.split("/");
                var pacienteId = Long.parseLong(parts[2]);
                var consultaId = Long.parseLong(parts[4]);
                var resultado = resultadoService.findByPacienteIdAndConsultaId(pacienteId, consultaId);
                String json = gson.toJson(resultado);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            // GET /pacientes/{id}/resultados
            if (path.matches("/pacientes/\\d+/resultados")) {
                String[] parts = path.split("/");
                var pacienteId = Long.parseLong(parts[2]);
                var resultados = resultadoService.findByPacienteId(pacienteId);
                String json = gson.toJson(resultados);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            // GET /pacientes/{id}/receitas
            if (path.matches("/pacientes/\\d+/receitas")) {
                String[] parts = path.split("/");
                var pacienteId = Long.parseLong(parts[2]);
                var receitas = receitaService.findByPacienteId(pacienteId);
                String json = gson.toJson(receitas);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            // Se nenhuma rota corresponder
            HttpUtils.enviarErro(exchange, "Rota não encontrada", 404);

        } catch (BadRequestException e) {
            HttpUtils.enviarErro(exchange, e.getMessage(), e.getHttpStatus());
        } catch (NumberFormatException e) {
            HttpUtils.enviarErro(exchange, "ID inválido", 400);
        } catch (Exception e) {
            HttpUtils.enviarErro(exchange, "Erro interno do servidor: " + e.getMessage(), 500);
        }
    }


}
