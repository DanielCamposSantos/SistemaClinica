package io.github.danielcampossantos.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
    private HttpUtils() {
    }

    public static void enviarResposta(HttpExchange exchange, String resposta, int statusCode, String contentType)
            throws IOException {
        byte[] respostaBytes = resposta.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, respostaBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(respostaBytes);
        }
    }

    public static void enviarErro(HttpExchange exchange, String mensagem, int statusCode) throws IOException {
        String erro = "{\"erro\":\"%s\"}".formatted(mensagem);
        enviarResposta(exchange, erro, statusCode, "application/json");
    }

    public static void setMethods(HttpExchange exchange, GetHandler handleGet) throws IOException {
        String metodo = exchange.getRequestMethod();

        if (metodo.equals("OPTIONS")) {
            handleOptions(exchange);
            return;
        }

        if (metodo.equals("GET")) {
            handleGet.handleGet(exchange);
        }
    }

    private static void handleOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(204, -1);
    }

}
