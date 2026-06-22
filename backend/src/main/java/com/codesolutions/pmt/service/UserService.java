package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.User;
import com.codesolutions.pmt.exception.NotFoundException;
import com.codesolutions.pmt.repository.UserRepository;
import org.springframework.stereotype.Service;

/** Acces aux utilisateurs (resolution de l'utilisateur courant, recherche par e-mail). */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Aucun utilisateur avec l'e-mail: " + email));
    }
}
