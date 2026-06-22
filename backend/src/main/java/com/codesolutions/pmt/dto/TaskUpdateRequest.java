package com.codesolutions.pmt.dto;

import com.codesolutions.pmt.domain.Priority;
import com.codesolutions.pmt.domain.TaskStatus;

import java.time.LocalDate;

/**
 * Mise a jour partielle d'une tache : tout champ nul est ignore.
 * Permet de changer n'importe quelle information ou d'ajouter une date de fin.
 */
public record TaskUpdateRequest(
        String name,
        String description,
        LocalDate dueDate,
        LocalDate endDate,
        Priority priority,
        TaskStatus status,
        Long assigneeId
) {
}
