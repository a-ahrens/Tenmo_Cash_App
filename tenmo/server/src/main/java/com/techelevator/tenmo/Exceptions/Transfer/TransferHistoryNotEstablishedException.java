package com.techelevator.tenmo.Exceptions.Transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "No transfer history found for this account")
public class TransferHistoryNotEstablishedException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransferHistoryNotEstablishedException() {
        super("No transfer history found for this account");
    }
}