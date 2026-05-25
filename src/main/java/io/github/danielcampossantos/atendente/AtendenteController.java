package io.github.danielcampossantos.atendente;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.utils.HttpUtils;

import java.io.IOException;

public class AtendenteController implements HttpHandler {

    private static AtendenteController instance;

    private final AtendenteService service;
    private final Gson gson;

    private AtendenteController() {
        service = AtendenteService.getInstance();
        gson = new Gson();
    }

    public static AtendenteController getInstance() {
        if (instance == null) {
            instance = new AtendenteController();
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
            //GET /atendentes/{id} - Buscar atendente por ID
            if (path.matches("/atendentes/\\d+")) {
                String[] parts = path.split("/");
                var id = Long.parseLong(parts[parts.length - 1]);
                var atendente = service.findById(id);
                String json = gson.toJson(atendente);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            //GET /atendentes - Listar todos os atendentes
            if (path.equals("/atendentes")) {
                var listaAtendentes = service.findAll();
                String json = gson.toJson(listaAtendentes);
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
