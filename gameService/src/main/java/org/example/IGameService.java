package org.example;

import java.io.IOException;
import java.util.List;

public interface IGameService {
    boolean checkUserExists(String username) throws GameException;
    User findLoggedInUser(String username) throws GameException, IOException;
    void login(User user, IGameObserver client) throws GameException, IOException;
    void logout(User user, IGameObserver client) throws GameException;
    Configuration chooseConfig() throws GameException;
    boolean checkCorrectPosition(int posX, int posY) throws GameException;
    void saveConfiguration(int posX, int posY, String message) throws GameException, IOException;
    void saveWinner(String username, int tries, String message) throws GameException, IOException;
    List<Winner> getWinners() throws GameException;
}
