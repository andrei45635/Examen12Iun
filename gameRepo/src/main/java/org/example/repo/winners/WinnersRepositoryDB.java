package org.example.repo.winners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.User;
import org.example.Winner;
import org.example.utils.JDBCutils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WinnersRepositoryDB implements WinnersRepository{
    private JDBCutils jdbCutils;
    private static final Logger logger = LogManager.getLogger();

    public WinnersRepositoryDB(Properties props) {
        logger.info("Initializing Repository with {}", props);
        jdbCutils = new JDBCutils(props);
    }
    @Override
    public List<Winner> getAll() {
        logger.traceEntry();
        List<Winner> winners = new ArrayList<>();

        String query = "SELECT * FROM winners";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int winnerID = resultSet.getInt("winnerid");
                    String username = resultSet.getString("username");
                    int tries = resultSet.getInt("tries");
                    String message = resultSet.getString("message");
                    Winner winner = new Winner(username, tries, message);
                    winner.setId(winnerID);
                    winners.add(winner);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return winners;
    }

    @Override
    public boolean delete(Winner entity) throws IOException {
        return false;
    }

    @Override
    public Winner update(Winner entity) throws IOException {
        return null;
    }

    @Override
    public Winner save(Winner entity) throws IOException {
        logger.traceEntry("saving winner {}", entity);

        String query = "INSERT INTO winners(username, tries, message) VALUES (?, ?, ?)";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setInt(2, entity.getTries());
            preparedStatement.setString(3, entity.getWinningHint());
            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {}", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return entity;
    }

    @Override
    public int size() {
        return 0;
    }
}
