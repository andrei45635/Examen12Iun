package org.example;

import org.example.repo.users.UserRepository;
import org.example.repo.users.UserRepositoryDB;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (
                IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        UserRepository userRepository = new UserRepositoryDB(props);
        List<User> users = userRepository.getAll();
        for(User u: users){
            System.out.println(u);
        }
    }
}