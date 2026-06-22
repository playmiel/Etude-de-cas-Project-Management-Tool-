package com.codesolutions.pmt.dto;

/** Representation publique d'un utilisateur (sans mot de passe). */
public record UserDto(Long id, String username, String email) {
}
