package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.GameException;
import org.example.IGameService;
import org.example.User;

import java.io.IOException;
import java.util.Random;

public class LoginController {
    @FXML
    private Button loginBtn;
    @FXML
    private TextField usernameTF;

    private IGameService server;
    private UserController userController;
    Parent mainUserParent;
    public void setServer(IGameService server) {
        this.server = server;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public void setParent(Parent p) {
        mainUserParent = p;
    }

    @FXML
    private void onClickLogin(ActionEvent actionEvent) {
        User loggedInUser = new User(usernameTF.getText());
        System.out.println(loggedInUser);
        try {
            server.login(loggedInUser, userController);
            userController.setServer(server);
            userController.setConfig();
            Stage stage = new Stage();
            stage.setScene(new Scene(mainUserParent, 600, 400));
            stage.setTitle("Hello!");
            stage.show();
            userController.setLoggedInUser(loggedInUser);
            Stage thisStage = (Stage) loginBtn.getScene().getWindow();
            thisStage.close();
        } catch (GameException | IOException cx) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Viewer");
            alert.setHeaderText("Authentication failure");
            alert.setContentText("Wrong username");
            alert.showAndWait();
        }
    }
}