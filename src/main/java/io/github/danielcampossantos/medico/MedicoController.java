package io.github.danielcampossantos.medico;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.danielcampossantos.exception.BadRequestException;
import io.github.danielcampossantos.utils.HttpUtils;

import java.io.IOException;

public class MedicoController implements HttpHandler {

    private static MedicoController instance;

    private final MedicoService service;
    private final Gson gson;

    private MedicoController() {
        service = MedicoService.getInstance();
        gson = new Gson();
    }

    public static MedicoController getInstance() {
        if (instance == null) {
            instance = new MedicoController();
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
            //GET /medicos/{id} - Buscar medico por ID
            if (path.matches("/medicos/\\d+")) {
                String[] parts = path.split("/");
                var id = Long.parseLong(parts[parts.length - 1]);
                var medico = service.findById(id);
                String json = gson.toJson(medico);
                HttpUtils.enviarResposta(exchange, json, 200, CONTENT_TYPE);
                return;
            }

            //GET /medicos - Listar todos os medicos
            if (path.equals("/medicos")) {
                var listaMedicos = service.findAll();
                String json = gson.toJson(listaMedicos);
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
