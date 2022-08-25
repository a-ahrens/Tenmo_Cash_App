package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;

public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Account getAccountById(long accountId) {
        Account account= null;
        String sql = "SELECT account_id, user_id, balance FROM account " +
                "WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if(results.next()){
            account= mapRowToAccount(results);
        }
        return account;
    }


    @Override
    public Account getAccountByUserId(long userId) {
        Account account= null;
        String sql = "SELECT account_id, user_id, balance FROM account " +
                "WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if(results.next()){
            account= mapRowToAccount(results);
        }
        return account;
    }


//
//    @Override
//    public Account createAccount(long userId) {
//
//
//
//
//        return null;
//    }

    @Override
    public void addToAccountBalance(long accountId, BigDecimal amount) {
       Account updatedAccount= getAccountById(accountId);
       updatedAccount.addToBalance(amount);
        String sql = "UPDATE account " +
                "SET balance = ? " +
                "WHERE account_id = ?";
       jdbcTemplate.update(sql, updatedAccount.getBalance(), accountId);


    }

    @Override
    public void subtractFromAccountBalance(long accountId, BigDecimal amount) {
        Account updatedAccount= getAccountById(accountId);
        updatedAccount.subtractFromBalance(amount);
        String sql = "UPDATE account " +
                "SET balance = ? " +
                "WHERE account_id = ?";
        jdbcTemplate.update(sql, updatedAccount.getBalance(), accountId);
    }

    private Account mapRowToAccount(SqlRowSet rs){
        Account account= new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setUserId(rs.getLong("user_id"));
        return account;



    }



}
