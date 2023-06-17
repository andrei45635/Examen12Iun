package org.example;

import org.example.repo.configurations.ConfigurationRepository;
import org.example.repo.configurations.ConfigurationRepositoryDB;
import org.example.repo.users.UserRepository;
import org.example.repo.winners.WinnersRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameService implements IGameService{
    private UserRepository userRepository;
    private ConfigurationRepositoryDB configurationRepository;
    private WinnersRepository winnersRepository;
    private Map<String, IGameObserver> loggedInClients;

    public GameService(UserRepository userRepository, ConfigurationRepositoryDB configurationRepository, WinnersRepository winnersRepository) {
        this.userRepository = userRepository;
        this.configurationRepository = configurationRepository;
        this.winnersRepository = winnersRepository;
        loggedInClients = new ConcurrentHashMap<>();
    }

    private void notifyObservers() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (var c : loggedInClients.entrySet()) {
            IGameObserver obs = loggedInClients.get(c.getKey());
            System.out.println("observer " + obs);
            if (obs != null) {
                executorService.execute(() -> {
                    try {
                        System.out.println("Updating winner list...");
                        obs.updateTaskList();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        executorService.shutdown();
    }
    @Override
    public boolean checkUserExists(String username) throws GameException {
        return userRepository.findUser(username);
    }

    @Override
    public synchronized User findLoggedInUser(String username) throws GameException, IOException {
        for (User u : userRepository.getAll()) {
            if (Objects.equals(u.getUsername(), username)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public synchronized void login(User user, IGameObserver client) throws GameException, IOException {
        User loggedUser = findLoggedInUser(user.getUsername());
        if (loggedUser != null) {
            if (loggedInClients.containsKey(loggedUser.getUsername())) {
                throw new GameException("User is already logged in!");
            }
            loggedInClients.put(loggedUser.getUsername(), client);
            System.out.println("logged in");
        } else {
            throw new GameException("Authentication failed.");
        }
    }

    @Override
    public synchronized void logout(User user, IGameObserver client) throws GameException {
        IGameObserver localClient = loggedInClients.remove(user.getUsername());
        if (localClient == null)
            throw new GameException("User " + user.getId() + " is not logged in!");
    }

    @Override
    public synchronized Configuration chooseConfig() throws GameException {
        List<Configuration> configs = configurationRepository.getAll();
        Random random = new Random();
        int randomIndex = random.nextInt(configs.size());
        return configs.get(randomIndex);
    }

    @Override
    public boolean checkCorrectPosition(int posX, int posY) throws GameException {
        return false;
    }

    @Override
    public synchronized void saveConfiguration(int posX, int posY, String message) throws GameException, IOException {
        configurationRepository.initialize();
        Configuration configuration = new Configuration(posX, posY, message);
        configurationRepository.save(configuration);
        configurationRepository.close();

    }

    @Override
    public synchronized void saveWinner(String username, int tries, String message) throws GameException, IOException {
        Winner winner = new Winner(username, tries, message);
        winnersRepository.save(winner);
        notifyObservers();
    }

    @Override
    public List<Winner> getWinners() throws GameException {
        return winnersRepository.getAll();
    }
}
