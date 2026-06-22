package com.codesolutions.pmt.dto;

import com.codesolutions.pmt.domain.Priority;
import com.codesolutions.pmt.domain.TaskStatus;

import java.time.LocalDate;

public record TaskDto(
        Long id,
        Long projectId,
        String name,
        String description,
        LocalDate dueDate,
        LocalDate endDate,
        Priority priority,
        TaskStatus status,
        UserDto assignee
) {
}
