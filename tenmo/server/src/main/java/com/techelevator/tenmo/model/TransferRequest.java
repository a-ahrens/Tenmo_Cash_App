package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferRequest {


    private long fromAccount;
    private long toAccount;
    private BigDecimal transferAmount;


    public TransferRequest() {
    }


    public TransferRequest(long toAccount, BigDecimal transferAmount) {
        this.toAccount = toAccount;
        this.transferAmount = transferAmount;
    }



    public long getToAccount() {
        return toAccount;
    }

    public void setToAccount(long toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }
}
