package com.codesolutions.pmt.dto;

import com.codesolutions.pmt.domain.Role;

import java.time.LocalDate;

/** Projet retourne avec le role de l'utilisateur courant sur ce projet. */
public record ProjectDto(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        Role currentUserRole
) {
}
