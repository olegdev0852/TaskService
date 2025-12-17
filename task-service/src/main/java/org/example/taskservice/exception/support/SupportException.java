package org.example.taskservice.exception.support;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SupportException extends RuntimeException {

    private final String debugDetails;

    private final HttpStatus httpStatus;

    public SupportException(String message, String debugDetails, Throwable cause) {
        super(message, cause);
        this.debugDetails = debugDetails;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    }
}
