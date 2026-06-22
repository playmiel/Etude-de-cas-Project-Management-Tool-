package com.codesolutions.pmt.dto;

import com.codesolutions.pmt.domain.Role;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(@NotNull Role role) {
}
