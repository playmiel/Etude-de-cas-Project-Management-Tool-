package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.Notification;
import com.codesolutions.pmt.domain.Task;
import com.codesolutions.pmt.domain.User;
import com.codesolutions.pmt.dto.NotificationDto;
import com.codesolutions.pmt.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Persiste les notifications et delegue l'envoi de l'e-mail. */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MembershipService membershipService;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                               MembershipService membershipService,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.membershipService = membershipService;
        this.emailService = emailService;
    }

    /** US: notifier par e-mail lorsqu'une tache est assignee. */
    @Transactional
    public void notifyAssignment(Task task, User assignee) {
        String message = "Une tache vous a ete assignee : '" + task.getName() + "'.";
        notificationRepository.save(new Notification(assignee, task, message));
        emailService.send(assignee.getEmail(), "PMT - Nouvelle tache assignee", message);
    }

    /** US: tous les roles peuvent etre notifies / consulter leurs notifications. */
    @Transactional(readOnly = true)
    public List<NotificationDto> listForUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(DtoMapper::toNotificationDto)
                .toList();
    }
}
