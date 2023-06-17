package org.example.network;

import org.example.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class GameReflectionWorker implements Runnable, IGameObserver {
    private IGameService server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public GameReflectionWorker(IGameService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        System.out.println("HandlerName " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return response;
    }

    private Response handleLOGIN(Request request) {
        System.out.println("Login request... " + request.type());
        User user = (User) request.data();
        System.out.println("Logging user " + user.toString() + " in");
        try {
            server.login(user, this);
            return okResponse;
        } catch (GameException e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Response handleLOGOUT(Request request) {
        System.out.println("Logout request... " + request.type());
        User user = (User) request.data();
        System.out.println("Logging user " + user.toString() + " out");
        try {
            server.logout(user, this);
            connected = false;
            return okResponse;
        } catch (GameException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleCHOSEN_CONFIG(Request request){
        System.out.println("Choosing configuration at random... " + request.type());
        try{
            return new Response.Builder().type(ResponseType.CHOSEN_CONFIG).data(server.chooseConfig()).build();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    private Response handleSAVE_CONFIG(Request request){
        System.out.println("Saving configuration request... " + request.type());
        Configuration configuration = (Configuration) request.data();
        System.out.println("Saving the configuration " + configuration.toString() + "...");
        try{
            server.saveConfiguration(configuration.getPosX(), configuration.getPosY(), configuration.getMessage());
            return new Response.Builder().type(ResponseType.SAVE_CONFIG).build();
        } catch (GameException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Response handleWON(Request request){
        System.out.println("User won the match... " + request.type());
        Winner winner = (Winner) request.data();
        try{
            server.saveWinner(winner.getUsername(), winner.getTries(), winner.getWinningHint());
            return new Response.Builder().type(ResponseType.WON).build();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response handleGET_WINNERS(Request request){
        System.out.println("Getting all the winners...");
        try{
            return new Response.Builder().type(ResponseType.GET_WINNERS).data(server.getWinners()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTaskList() throws GameException {
        Response response = new Response.Builder().type(ResponseType.GET_UPDATE_TASKS).build();
        System.out.println("Updating winners list...");
        try {
            sendResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
