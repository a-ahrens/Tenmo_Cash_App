package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserAccount;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public List<UserAccount> findAll() {
        List<UserAccount> users = new ArrayList<>();
        String sql = "SELECT tenmo_user.username, account_id " +
                     "FROM account " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "ORDER BY username;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            UserAccount user = mapRowToUserAccount(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public List<UserAccount> findOtherUsers(long userId) {
        List<UserAccount> users = new ArrayList<>();
        String sql = "SELECT tenmo_user.username, account_id " +
                     "FROM account " +
                     "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                     "WHERE tenmo_user.user_id != ? " +
                     "ORDER BY username;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,userId);
        while(results.next()) {
            UserAccount user = mapRowToUserAccount(results);
            users.add(user);
        }
        return users;
    }




    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    //@Transactional
    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }

        Account newAccount= new Account();
        String accountSql= "INSERT INTO account (user_id, balance) VALUES(?, ?) RETURNING account_id;";
        try {
            Long newAccountId = jdbcTemplate.queryForObject(accountSql, Long.class, newUserId, newAccount.getBalance());
        }catch(DataAccessException e){
            return false;
        }
        return true;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }

    private UserAccount mapRowToUserAccount(SqlRowSet rs) {
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountId(rs.getLong("account_id"));
        userAccount.setUsername(rs.getString("username"));
        return userAccount;
    }
}
