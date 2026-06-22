package com.codesolutions.pmt.exception;

/** Conflit avec l'etat courant (ex: email deja utilise) (HTTP 409). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
