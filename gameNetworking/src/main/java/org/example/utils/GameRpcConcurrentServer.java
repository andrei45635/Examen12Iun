package org.example.utils;

import org.example.IGameService;
import org.example.network.GameReflectionWorker;

import java.net.Socket;

public class GameRpcConcurrentServer extends AbsConcurrentServer {
    private IGameService gameService;
    public GameRpcConcurrentServer(int port, IGameService gameService) {
        super(port);
        this.gameService = gameService;
        System.out.println("Game - GameConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        GameReflectionWorker worker = new GameReflectionWorker(gameService, client);
        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
