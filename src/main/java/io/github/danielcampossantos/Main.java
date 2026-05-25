package io.github.danielcampossantos;

import com.sun.net.httpserver.HttpServer;
import io.github.danielcampossantos.atendente.AtendenteController;
import io.github.danielcampossantos.medico.MedicoController;
import io.github.danielcampossantos.paciente.PacienteController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/pacientes", PacienteController.getInstance());
        server.createContext("/medicos", MedicoController.getInstance());
        server.createContext("/atendentes", AtendenteController.getInstance());
        server.setExecutor(null);
        server.start();

        System.out.println("Sevidor iniciado na porta 8080");

    }
}
