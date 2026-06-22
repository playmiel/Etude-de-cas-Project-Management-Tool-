package com.codesolutions.pmt.web.error;

import java.time.Instant;
import java.util.Map;

/** Corps de reponse standardise pour les erreurs de l'API. */
public class ApiError {

    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;

    public ApiError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
