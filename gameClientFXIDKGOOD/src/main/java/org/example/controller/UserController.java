package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.example.*;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements IGameObserver {
    @FXML
    private TableView<Winner> leaderboardTreeView;
    @FXML
    private TableColumn<Winner, String> usernameColumn;
    @FXML
    private TableColumn<Winner, Integer> triesColumn;
    @FXML
    private TableColumn<Winner, String> winningHintColumn;
    @FXML
    private GridPane gameGridPane;
    private final ObservableList<Winner> winnersModel = FXCollections.observableArrayList();
    private IGameService server;
    private User loggedInUser;
    private Configuration config;
    private int tries = 0;
    private Button[][] buttons = new Button[4][4]; // 4x4 grid of buttons
    public UserController() {}

    public void setServer(IGameService server) throws GameException {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("Username"));
        triesColumn.setCellValueFactory(new PropertyValueFactory<>("Tries"));
        winningHintColumn.setCellValueFactory(new PropertyValueFactory<>("WinningHint"));

        leaderboardTreeView.setItems(winnersModel);

        this.server = server;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Button button = new Button("Button " + row + "-" + col);
                button.setOnAction(new ButtonClickHandler(row, col));
                buttons[row][col] = button;
                gameGridPane.add(button, col, row);
            }
        }
        initModel();
    }

    private void initModel() throws GameException {
        winnersModel.setAll(server.getWinners());
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("Username"));
        triesColumn.setCellValueFactory(new PropertyValueFactory<>("Tries"));
        winningHintColumn.setCellValueFactory(new PropertyValueFactory<>("WinningHint"));

        leaderboardTreeView.setItems(winnersModel);
    }

    public void setConfig() throws GameException {
        this.config = this.server.chooseConfig();
        System.out.println(config.toString());
    }

    private class ButtonClickHandler implements EventHandler<ActionEvent> {
        private int row;
        private int col;

        public ButtonClickHandler(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void handle(ActionEvent event) {
            Button clickedButton = buttons[row][col];
            int clickedRow = GridPane.getRowIndex(clickedButton);
            int clickedCol = GridPane.getColumnIndex(clickedButton);
            System.out.println("Button clicked at row: " + clickedRow + ", col: " + clickedCol);
            boolean won = false;
            if (clickedRow == config.getPosX() && clickedCol == config.getPosY()) {
                tries++;
                clickedButton.setText(config.getMessage());
                won = true;
            } else {
                double dist = Point2D.distance(clickedRow, clickedCol, config.getPosX(), config.getPosY());
                clickedButton.setText(String.valueOf(dist));
                tries++;
                System.out.println("TRIES: " + tries);
            }
            if(!won && tries >= 4){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("You lost!!! :(");
                alert.setHeaderText("Results:");
                alert.setContentText(config.getMessage());
                alert.showAndWait();
                try {
                    server.saveWinner(loggedInUser.getUsername(), tries, "LOST");
                } catch (GameException | IOException e) {
                    throw new RuntimeException(e);
                }
            } else if(won && tries <= 4){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("You won!!! :D");
                alert.setHeaderText("Results:");
                alert.setContentText(config.getMessage() + " in " + tries + " tries");
                alert.showAndWait();
                try {
                    server.saveWinner(loggedInUser.getUsername(), tries, config.getMessage());
                } catch (GameException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    @Override
    public void updateTaskList() throws GameException {
        Platform.runLater(() -> {
            List<Winner> winners;
            try {
                winners = server.getWinners();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
            winnersModel.setAll(winners);
        });
    }
}
