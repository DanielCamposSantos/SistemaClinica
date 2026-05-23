package io.github.danielcampossantos.paciente;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.danielcampossantos.domain.Paciente;
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

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();

        if (metodo.equals("GET")) {
            handleGet(exchange);
        }

        if (metodo.equals("POST")) {
            handlePost(exchange);
        }
    }


    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches(".*/\\d+/prontuario")) {
            try {
                String[] parts = path.split("/");
                var id = Long.parseLong(parts[2]);
                var prontuario = service.findProntuarioByPacienteId(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
                String json = gson.toJson(prontuario);
                HttpUtils.enviarResposta(exchange,json,200,"application/json");
            } catch (BadRequestException e) {
                HttpUtils.enviarErro(exchange,e.getMessage(),e.getHttpStatus());
            }
        }


        var listaDePacientes = service.findAll();
        String json = gson.toJson(listaDePacientes);

        HttpUtils.enviarResposta(exchange, json, 200, "application/json");

    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = HttpUtils.lerBody(exchange);

        if (body.isEmpty()) {
            HttpUtils.enviarErro(exchange, "O body está vazio", 400);
        }

        Paciente pacienteToAdd = gson.fromJson(body, Paciente.class);
        service.save(pacienteToAdd);

        HttpUtils.enviarResposta(exchange, "", 201, "application/json");
    }
}
