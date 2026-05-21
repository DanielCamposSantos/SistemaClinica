package io.github.danielcampossantos;

import com.sun.net.httpserver.HttpServer;
import io.github.danielcampossantos.controller.PacienteController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/pacientes", PacienteController.getInstance());
        server.setExecutor(null);
        server.start();

        System.out.println("Sevidor iniciado na porta 8080");

    }
}
