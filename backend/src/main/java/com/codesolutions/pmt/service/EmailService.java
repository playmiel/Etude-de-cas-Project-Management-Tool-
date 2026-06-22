package com.codesolutions.pmt.service;

/** Abstraction d'envoi d'e-mail (notifications d'assignation de tache). */
public interface EmailService {
    void send(String to, String subject, String body);
}
