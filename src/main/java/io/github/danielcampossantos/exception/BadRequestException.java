package io.github.danielcampossantos.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private int httpStatus;

    public BadRequestException(String message) {
        super(message);
        this.httpStatus = 400;
    }
}
