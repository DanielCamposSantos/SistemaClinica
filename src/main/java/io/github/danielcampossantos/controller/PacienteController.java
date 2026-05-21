package io.github.danielcampossantos.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.danielcampossantos.service.PacienteService;
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
            var listaDePacientes = service.findAll();
            String json = gson.toJson(listaDePacientes);

            HttpUtils.enviarResposta(exchange, json, 200, "application/json");

        }
    }
}
