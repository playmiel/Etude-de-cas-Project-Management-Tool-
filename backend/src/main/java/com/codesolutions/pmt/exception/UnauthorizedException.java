package com.codesolutions.pmt.exception;

/** Echec d'authentification (HTTP 401). */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
