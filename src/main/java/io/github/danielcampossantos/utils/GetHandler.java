package io.github.danielcampossantos.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

@FunctionalInterface
public interface GetHandler {
    void handleGet(HttpExchange exchange) throws IOException;
}
