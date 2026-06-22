package com.codesolutions.pmt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank @Size(max = 150) String name,
        String description,
        LocalDate startDate
) {
}
