package com.pragma.bootcamp.usecase.report.exceptions;

public class InconsistentDataException extends RuntimeException {
    public InconsistentDataException(String message) {
        super(message);
    }
}
