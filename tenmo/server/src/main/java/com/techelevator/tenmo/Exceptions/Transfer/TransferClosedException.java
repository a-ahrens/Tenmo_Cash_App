package com.techelevator.tenmo.Exceptions.Transfer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.FORBIDDEN, reason = "Transfer's Status has been finalized. No changes permitted.")
public class TransferClosedException extends Exception {
    private static final long serialVersionUID = 1L;

    public TransferClosedException() {
        super("Transfer's Status has been finalized. No changes permitted.");
    }
}

