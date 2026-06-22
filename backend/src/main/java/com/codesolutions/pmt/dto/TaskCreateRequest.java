package com.codesolutions.pmt.dto;

import com.codesolutions.pmt.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank @Size(max = 150) String name,
        String description,
        LocalDate dueDate,
        Priority priority,
        Long assigneeId
) {
}
