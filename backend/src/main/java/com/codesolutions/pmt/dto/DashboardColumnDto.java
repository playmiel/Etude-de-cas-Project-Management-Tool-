package com.codesolutions.pmt.dto;

import com.codesolutions.pmt.domain.TaskStatus;

import java.util.List;

/** Une colonne du tableau de bord : un statut et les taches associees. */
public record DashboardColumnDto(TaskStatus status, List<TaskDto> tasks) {
}
