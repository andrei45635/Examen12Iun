package org.example.repo.users;

import org.example.User;
import org.example.repo.IRepository;

public interface UserRepository extends IRepository<Integer, User> {
    boolean findUser(String username);
}
