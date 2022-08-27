package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transfer {

    private long transferId;
    private long fromAccount;
    private long toAccount;
    private BigDecimal transferAmount;
    private LocalDateTime timeStamp;
    private String status= "Pending";
    private String description = " ";

    public Transfer() {
    }

    public Transfer(long transferId, long fromAccount, long toAccount, BigDecimal transferAmount, LocalDateTime timeStamp, String status, String description) {
        this.transferId = transferId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transferAmount = transferAmount;
        this.timeStamp = timeStamp;
        this.status = status;
        this.description = description;
    }


    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(long fromAccount) {
        this.fromAccount = fromAccount;
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

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
