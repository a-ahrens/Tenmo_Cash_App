package com.techelevator.tenmo.Exceptions.Request;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Requester cannot be the Receiver")
public class RequestSameAccountException extends Exception {
    private static final long serialVersionUID = 1L;

    public RequestSameAccountException() {
        super("Requester cannot be the Receiver");
    }
}

