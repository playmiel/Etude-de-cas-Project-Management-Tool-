package com.codesolutions.pmt.exception;

/** Action interdite pour le role de l'utilisateur courant (HTTP 403). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
