package com.pm.flightsmanagement.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String ex) {
        super(ex);
    }
}
