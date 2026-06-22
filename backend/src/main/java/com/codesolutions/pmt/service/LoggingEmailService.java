package com.codesolutions.pmt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation d'envoi d'e-mail. Si un serveur SMTP est configure, l'e-mail est
 * reellement expedie ; sinon (dev/test), il est journalise. Cela evite de bloquer
 * l'application en l'absence de SMTP.
 */
@Service
public class LoggingEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${pmt.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${pmt.mail.from:noreply@pmt.local}")
    private String from;

    public LoggingEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String to, String subject, String body) {
        if (!mailEnabled) {
            log.info("[EMAIL simule] a={}, sujet='{}', corps='{}'", to, subject, body);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("[EMAIL envoye] a={}, sujet='{}'", to, subject);
    }
}
