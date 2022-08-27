package com.techelevator.tenmo.Exceptions.Transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Transfer not found for this account")
public class TransferIdNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

        public TransferIdNotFoundException() {
            super("Transfer not found for this account");
        }
}

