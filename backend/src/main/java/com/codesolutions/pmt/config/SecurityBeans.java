package com.codesolutions.pmt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Fournit uniquement l'encodeur de mot de passe (BCrypt).
 * Conformement a l'enonce, la chaine de filtres Spring Security n'est pas activee :
 * seule la primitive de hachage est utilisee.
 */
@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
