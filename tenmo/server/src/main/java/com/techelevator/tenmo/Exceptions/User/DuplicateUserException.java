package com.techelevator.tenmo.Exceptions.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.BAD_REQUEST, reason = "Username already exists.")
public class DuplicateUserException extends Exception {
    private static final long serialVersionUID = 1L;

    public DuplicateUserException() {
        super("Username already exists.");
    }
}

