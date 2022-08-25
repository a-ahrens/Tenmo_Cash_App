package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    // get account by ID
    // get account by userID
    // create account


    Account getAccountByAccountId(long accountId);

    Account getAccountByUserId(long userId);

   // Account createAccount(long userId);

    void addToAccountBalance(long accountId, BigDecimal amount);

    void subtractFromAccountBalance(long accountId, BigDecimal amount);





}
