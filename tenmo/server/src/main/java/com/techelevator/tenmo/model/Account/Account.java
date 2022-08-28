package com.techelevator.tenmo.model.Account;

import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class Account {

    private long accountId;
    private long userId;
    @PositiveOrZero
    private BigDecimal balance = new BigDecimal("1000.00");

    public Account() {
    }

    public Account(long accountId, long userId, BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }


    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void addToBalance(BigDecimal amount) {
        balance= balance.add(amount);
    }

    public void subtractFromBalance(BigDecimal amount) {
        balance= balance.subtract(amount);
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }


}

