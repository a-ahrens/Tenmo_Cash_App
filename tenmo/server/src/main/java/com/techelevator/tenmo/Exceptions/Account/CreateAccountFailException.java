package com.techelevator.tenmo.Exceptions.Account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus( code = HttpStatus.NOT_FOUND, reason = "Account creation failed. Please try registering again.")
public class CreateAccountFailException extends Exception {
    private static final long serialVersionUID = 1L;

    public CreateAccountFailException() {
        super("Account creation failed. Please try registering again.");

    }
}