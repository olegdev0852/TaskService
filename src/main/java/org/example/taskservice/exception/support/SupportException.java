package org.example.taskservice.exception.support;

import lombok.Getter;

@Getter
public class SupportException extends RuntimeException {


    private final String debugDetails;


    public SupportException(String message, String debugDetails, Throwable cause) {
        super(message, cause);

        this.debugDetails = debugDetails;

    }
}
