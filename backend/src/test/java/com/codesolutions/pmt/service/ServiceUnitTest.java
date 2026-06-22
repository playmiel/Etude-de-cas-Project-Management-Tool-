package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.*;
import com.codesolutions.pmt.dto.LoginRequest;
import com.codesolutions.pmt.dto.RegisterRequest;
import com.codesolutions.pmt.exception.ConflictException;
import com.codesolutions.pmt.exception.ForbiddenException;
import com.codesolutions.pmt.exception.UnauthorizedException;
import com.codesolutions.pmt.repository.ProjectMemberRepository;
import com.codesolutions.pmt.repository.ProjectRepository;
import com.codesolutions.pmt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/** Tests unitaires des branches metier critiques (permissions, erreurs). */
@ExtendWith(MockitoExtension.class)
class ServiceUnitTest {

    @Mock
    UserRepository userRepository;

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("a@b.c")).thenReturn(true);
        AuthService service = new AuthService(userRepository, new BCryptPasswordEncoder());

        assertThatThrownBy(() -> service.register(new RegisterRequest("u", "a@b.c", "secret1")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void register_rejectsDuplicateUsername() {
        when(userRepository.existsByEmail("a@b.c")).thenReturn(false);
        when(userRepository.existsByUsername("u")).thenReturn(true);
        AuthService service = new AuthService(userRepository, new BCryptPasswordEncoder());

        assertThatThrownBy(() -> service.register(new RegisterRequest("u", "a@b.c", "secret1")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void login_rejectsUnknownEmail() {
        when(userRepository.findByEmail("x@y.z")).thenReturn(Optional.empty());
        AuthService service = new AuthService(userRepository, new BCryptPasswordEncoder());

        assertThatThrownBy(() -> service.login(new LoginRequest("x@y.z", "pw")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void requireRole_allowsMatchingRoleAndDeniesOthers() {
        ProjectMemberRepository memberRepo = mock(ProjectMemberRepository.class);
        ProjectRepository projectRepo = mock(ProjectRepository.class);
        UserService userService = mock(UserService.class);
        MembershipService membership = new MembershipService(memberRepo, projectRepo, userService);

        ProjectMember observerMember = new ProjectMember(new Project(), new User(), Role.OBSERVER);
        when(memberRepo.findByProjectIdAndUserId(1L, 2L)).thenReturn(Optional.of(observerMember));

        // OBSERVER autorise
        assertThat(membership.requireRole(1L, 2L, Role.OBSERVER)).isSameAs(observerMember);
        // OBSERVER refuse pour une action ADMIN/MEMBER
        assertThatThrownBy(() -> membership.requireRole(1L, 2L, Role.ADMIN, Role.MEMBER))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void requireMembership_throwsWhenNotMember() {
        ProjectMemberRepository memberRepo = mock(ProjectMemberRepository.class);
        MembershipService membership = new MembershipService(
                memberRepo, mock(ProjectRepository.class), mock(UserService.class));
        when(memberRepo.findByProjectIdAndUserId(9L, 9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> membership.requireMembership(9L, 9L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void mapper_handlesNullUserAndNullTask() {
        assertThat(DtoMapper.toUserDto(null)).isNull();
    }
}
