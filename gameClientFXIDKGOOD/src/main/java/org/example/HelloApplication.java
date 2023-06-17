package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.controller.UserController;
import org.example.network.GameServiceRpcProxy;

import java.io.IOException;
import java.util.Properties;

public class HelloApplication extends Application {
    private Stage primaryStage;
    private static int defaultContestPort = 55588;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(HelloApplication.class.getResourceAsStream("/gameclient.properties"));
            System.out.println("Client properties set.");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find gameclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("game.server.host", defaultServer);
        int serverPort = defaultContestPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("game.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultContestPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IGameService server = new GameServiceRpcProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/hello-view.fxml"));
        Parent root = loader.load();

        LoginController ctrl = loader.getController();
        ctrl.setServer(server);

        FXMLLoader cloader = new FXMLLoader(getClass().getClassLoader().getResource("views/test-view.fxml"));
        Parent croot = cloader.load();

        UserController userController = cloader.getController();
        //userController.setServer(server);

        ctrl.setUserController(userController);
        ctrl.setParent(croot);

        primaryStage.setTitle("Game Viewer");
        primaryStage.setScene(new Scene(root, 350, 300));
        primaryStage.show();
    }
}