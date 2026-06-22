package com.codesolutions.pmt.exception;

/** Ressource demandee introuvable (HTTP 404). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
