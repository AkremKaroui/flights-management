package com.pm.flightsmanagement.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String ex) {
        super(ex);
    }
}
