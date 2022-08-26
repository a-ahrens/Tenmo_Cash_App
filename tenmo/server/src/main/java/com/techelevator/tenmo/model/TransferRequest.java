package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NegativeOrZero;
import java.math.BigDecimal;

public class TransferRequest {


//    private long fromAccount;
    private long toAccount;
    @DecimalMin("0.01")
    private BigDecimal transferAmount;


    public TransferRequest() {
    }

//    public TransferRequest(long fromAccount, long toAccount, BigDecimal transferAmount) {
//        this.fromAccount = fromAccount;
//        this.toAccount = toAccount;
//        this.transferAmount = transferAmount;
//    }



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

//    public long getFromAccount() {
//        return fromAccount;
//    }
//
//    public void setFromAccount(long fromAccount) {
//        this.fromAccount = fromAccount;
//    }
}
