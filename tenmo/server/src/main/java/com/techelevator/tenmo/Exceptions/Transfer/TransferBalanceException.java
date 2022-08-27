package com.techelevator.tenmo.Exceptions.Transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Transfer declined. Insufficient balance in your account")
public class TransferBalanceException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransferBalanceException() {
        super("Transfer declined. Insufficient balance in your account");
    }
}


