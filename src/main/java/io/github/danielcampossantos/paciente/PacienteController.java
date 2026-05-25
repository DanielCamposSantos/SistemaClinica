package io.github.danielcampossantos.paciente;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.utils.HttpUtils;

import java.io.IOException;

public class PacienteController implements HttpHandler {

    private static PacienteController instance;

    private final PacienteService service;
    private final Gson gson;

    private PacienteController() {
        service = PacienteService.getInstance();
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
        String metodo = exchange.getRequestMethod();

        if (metodo.equals("GET")) {
            handleGet(exchange);
        }

    }


    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        try {
            //GET /pacientes/{id} - Buscar paciente por ID
            if (path.matches("/pacientes/\\d+")) {
                String[] parts = path.split("/");
                var id = Long.parseLong(parts[parts.length - 1]);
                var paciente = service.findById(id);
                String json = gson.toJson(paciente);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            //GET /pacientes - Listar todos os pacientes
            if (path.equals("/pacientes")) {
                var listaPacientes = service.findAll();
                String json = gson.toJson(listaPacientes);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }


            //GET /pacientes/prontuarios/{id} - Buscar prontuário específico (MAIS ESPECÍFICO PRIMEIRO)
            if (path.matches("/pacientes/prontuarios/\\d+")) {
                String[] parts = path.split("/");
                var id = Long.parseLong(parts[parts.length - 1]);
                var prontuario = service.findProntuarioByPacienteId(id);
                String json = gson.toJson(prontuario);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            //GET /pacientes/prontuarios - Listar todos os prontuários
            if (path.equals("/pacientes/prontuarios")) {
                var listaProntuarios = service.findAllProntuarios();
                String json = gson.toJson(listaProntuarios);
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
