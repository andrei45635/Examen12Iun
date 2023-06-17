package org.example;

import org.example.repo.configurations.ConfigurationRepositoryDB;
import org.example.repo.users.UserRepository;
import org.example.repo.users.UserRepositoryDB;
import org.example.repo.winners.WinnersRepository;
import org.example.repo.winners.WinnersRepositoryDB;
import org.example.utils.AbstractServer;
import org.example.utils.GameRpcConcurrentServer;
import org.example.utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55588;

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/gameServer.properties"));
            System.out.println("Server properties set.");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find gameServer.properties " + e);
            return;
        }
        UserRepository userRepository = new UserRepositoryDB(serverProps);
        ConfigurationRepositoryDB configurationRepository = new ConfigurationRepositoryDB();
        WinnersRepository winnersRepository = new WinnersRepositoryDB(serverProps);
        IGameService gameService = new GameService(userRepository, configurationRepository, winnersRepository);
        int gameServerPort = defaultPort;
        try {
            gameServerPort = Integer.parseInt(serverProps.getProperty("game.server.port"));
        } catch (NumberFormatException nef) {
            System.err.println("Wrong  Port Number" + nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + gameServerPort);
        AbstractServer server = new GameRpcConcurrentServer(gameServerPort, gameService);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }
}
