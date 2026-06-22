package com.codesolutions.pmt.web;

import com.codesolutions.pmt.dto.NotificationDto;
import com.codesolutions.pmt.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Notifications de l'utilisateur courant. */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Lister les notifications de l'utilisateur courant")
    @GetMapping
    public List<NotificationDto> list(@RequestHeader("X-User-Id") Long userId) {
        return notificationService.listForUser(userId);
    }
}
