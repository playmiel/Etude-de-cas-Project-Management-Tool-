package com.codesolutions.pmt.dto;

/** Assignation d'une tache : assigneeId nul = desassignation. */
public record AssignTaskRequest(Long assigneeId) {
}
