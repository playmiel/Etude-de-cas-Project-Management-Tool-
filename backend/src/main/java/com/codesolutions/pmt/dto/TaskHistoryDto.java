package com.codesolutions.pmt.dto;

import java.time.Instant;

public record TaskHistoryDto(
        Long id,
        String changeDescription,
        String changedByUsername,
        Instant changedAt
) {
}
