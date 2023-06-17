package org.example.network;

import org.checkerframework.checker.units.qual.C;
import org.example.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServiceRpcProxy implements IGameService {
    private String host;
    private int port;
    private IGameObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public GameServiceRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startReader() {
        Thread tw = new Thread(new GameServiceRpcProxy.ReaderThread());
        tw.start();
    }

    private void sendRequest(Request request) throws GameException {
        try {
            System.out.println(output);
            output.writeObject(request);
            output.flush();
        } catch (Exception e) {
            throw new GameException("Error sending object " + e);
        }
    }

    private Response readResponse() throws GameException {
        Response response;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            throw new GameException("Error sending object " + e);
        }
        return response;
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.GET_UPDATE_TASKS) {
            System.out.println("before update");
            try{
                client.updateTaskList();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
            System.out.println("after update");
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.GET_UPDATE_TASKS;
    }

    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("Response received " + response);
                    if (isUpdate((Response) response)) {
                        System.out.println("i'm an update" + response);
                        handleUpdate((Response) response);
                    } else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }

    @Override
    public boolean checkUserExists(String username) throws GameException {
        return false;
    }

    @Override
    public User findLoggedInUser(String username) throws GameException, IOException {
        return null;
    }

    @Override
    public void login(User user, IGameObserver client) throws GameException, IOException {
        initializeConnection();
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            return;
        }
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            closeConnection();
            throw new GameException(error);
        }
    }

    @Override
    public void logout(User user, IGameObserver client) throws GameException {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            throw new GameException(error);
        }
    }

    @Override
    public Configuration chooseConfig() throws GameException {
        Request request = new Request.Builder().type(RequestType.CHOSEN_CONFIG).build();
        System.out.println("Sending request to generate a configuration...");
        sendRequest(request);
        System.out.println("Reading the response...");
        Response response = readResponse();
        if(response.type() == ResponseType.CHOSEN_CONFIG){
            return (Configuration) response.data();
        } else {
            String error = response.data().toString() + " eroare";
            throw new GameException(error);
        }
    }

    @Override
    public boolean checkCorrectPosition(int posX, int posY) throws GameException {
        return false;
    }

    @Override
    public void saveConfiguration(int posX, int posY, String message) throws GameException {
        Configuration configuration = new Configuration(posX, posY, message);
        Request request = new Request.Builder().type(RequestType.SAVE_CONFIG).data(configuration).build();
        System.out.println("Sending request to save configuration... ");
        sendRequest(request);
        System.out.println("Reading the response...");
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString() + " eroare";
            throw new GameException(error);
        }
    }

    @Override
    public void saveWinner(String username, int tries, String message) throws GameException {
        Winner winner = new Winner(username, tries, message);
        Request request = new Request.Builder().type(RequestType.WON).data(winner).build();
        System.out.println("Sending request to save winner...");
        sendRequest(request);
        System.out.println("Reading the response...");
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString() + " eroare";
            throw new GameException(error);
        }
    }

    @Override
    public List<Winner> getWinners() throws GameException {
        Request request = new Request.Builder().type(RequestType.GET_WINNERS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.GET_WINNERS) {
            return (List<Winner>) response.data();
        }
        else if (response.type() == ResponseType.ERROR) {
            String error = response.data().toString();
            throw new GameException(error);
        }
        return null;
    }
}
