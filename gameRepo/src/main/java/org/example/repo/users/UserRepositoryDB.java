package org.example.repo.users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.User;
import org.example.utils.JDBCutils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserRepositoryDB implements UserRepository{
    private JDBCutils jdbCutils;
    private static final Logger logger = LogManager.getLogger();

    public UserRepositoryDB(Properties props) {
        logger.info("Initializing Repository with {}", props);
        jdbCutils = new JDBCutils(props);
    }

    @Override
    public List<User> getAll() {
        logger.traceEntry();
        List<User> users = new ArrayList<>();

        String query = "SELECT * FROM users";
        Connection connection = jdbCutils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    int userID = resultSet.getInt("userid");
                    String username = resultSet.getString("username");
                    User user = new User(username);
                    user.setId(userID);
                    //User user = new User(userID, username, password);
                    //user.setId(userID);
                    users.add(user);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("DB Error " + ex);
            throw new RuntimeException(ex);
        }

        return users;
    }

    @Override
    public boolean delete(User entity) throws IOException {
        return false;
    }

    @Override
    public User update(User entity) throws IOException {
        return null;
    }

    @Override
    public User save(User entity) throws IOException {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean findUser(String username) {
        logger.traceEntry("checking if user with given parameters exists");
        String query = "SELECT EXISTS (SELECT 1 FROM users WHERE username = ?)";
        Connection connection = jdbCutils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) == 1){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
