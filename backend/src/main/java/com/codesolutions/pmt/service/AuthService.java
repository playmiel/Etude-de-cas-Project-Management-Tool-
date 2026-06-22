package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.User;
import com.codesolutions.pmt.dto.LoginRequest;
import com.codesolutions.pmt.dto.RegisterRequest;
import com.codesolutions.pmt.dto.UserDto;
import com.codesolutions.pmt.exception.ConflictException;
import com.codesolutions.pmt.exception.UnauthorizedException;
import com.codesolutions.pmt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Inscription et connexion par e-mail / mot de passe. */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** US: en tant que visiteur, je veux pouvoir m'inscrire. */
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Cette adresse e-mail est deja utilisee.");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Ce nom d'utilisateur est deja utilise.");
        }
        User user = new User(request.username(), request.email(),
                passwordEncoder.encode(request.password()));
        return DtoMapper.toUserDto(userRepository.save(user));
    }

    /** US: en tant qu'inscrit, je veux pouvoir me connecter. */
    @Transactional(readOnly = true)
    public UserDto login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Identifiants invalides."));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Identifiants invalides.");
        }
        return DtoMapper.toUserDto(user);
    }
}
