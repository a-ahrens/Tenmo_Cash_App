package com.techelevator.tenmo.Exceptions.Request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( code = HttpStatus.FORBIDDEN, reason = "Request's Status has been finalized. No changes permitted.")
public class RequestClosedException extends Exception {
    private static final long serialVersionUID = 1L;

    public RequestClosedException() {
        super("Request's Status has been finalized. No changes permitted.");
    }
}

