package com.codesolutions.pmt.domain;

/**
 * Role d'un utilisateur sur un projet donne.
 * Le role est porte par l'entite {@link ProjectMember} : un meme utilisateur
 * peut avoir un role different selon le projet.
 */
public enum Role {
    ADMIN,
    MEMBER,
    OBSERVER
}
