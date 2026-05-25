package io.github.danielcampossantos.agendamento;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.utils.HttpUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AgendamentoController implements HttpHandler {

    private static AgendamentoController instance;

    private final AgendamentoService service;
    private final Gson gson;

    private AgendamentoController() {
        service = AgendamentoService.getInstance();
        gson = new Gson();
    }

    public static AgendamentoController getInstance() {
        if (instance == null) {
            instance = new AgendamentoController();
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
        String query = exchange.getRequestURI().getQuery();

        try {
            // GET /agendamentos?paciente=1
            if (path.equals("/agendamentos") && query != null && query.contains("paciente=")) {
                Map<String, String> params = parseQueryParams(query);
                var pacienteId = Long.parseLong(params.get("paciente"));
                var listaAgendamentos = service.findByPacienteId(pacienteId);
                String json = gson.toJson(listaAgendamentos);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            // GET /agendamentos?mes=1&ano=2024
            if (path.equals("/agendamentos") && query != null && query.contains("mes=")) {
                Map<String, String> params = parseQueryParams(query);
                var mes = Integer.parseInt(params.get("mes"));
                var ano = Integer.parseInt(params.getOrDefault("ano", String.valueOf(LocalDate.now().getYear())));
                var listaAgendamentos = service.findByMes(mes, ano);
                String json = gson.toJson(listaAgendamentos);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            // GET /agendamentos
            if (path.equals("/agendamentos")) {
                var listaAgendamentos = service.findAll();
                String json = gson.toJson(listaAgendamentos);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            HttpUtils.enviarErro(exchange, "Rota não encontrada", 404);

        } catch (BadRequestException e) {
            HttpUtils.enviarErro(exchange, e.getMessage(), e.getHttpStatus());
        } catch (NumberFormatException e) {
            HttpUtils.enviarErro(exchange, "Parâmetro inválido", 400);
        } catch (Exception e) {
            HttpUtils.enviarErro(exchange, "Erro interno do servidor: " + e.getMessage(), 500);
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
}