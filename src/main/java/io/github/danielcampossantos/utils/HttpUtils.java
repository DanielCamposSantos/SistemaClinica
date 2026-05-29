package io.github.danielcampossantos.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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


}
