package com.techelevator.tenmo.Exceptions.Request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Request not found for this account")
public class RequestNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public RequestNotFoundException() {
        super("Request not found for this account");
    }
}

