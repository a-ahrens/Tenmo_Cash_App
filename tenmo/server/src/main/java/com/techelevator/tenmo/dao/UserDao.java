package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserAccount;

import java.util.List;

public interface UserDao {

    List<UserAccount> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    List<UserAccount> findOtherUsers(long userId);
}
