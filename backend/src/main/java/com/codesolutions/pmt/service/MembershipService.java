package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.Project;
import com.codesolutions.pmt.domain.ProjectMember;
import com.codesolutions.pmt.domain.Role;
import com.codesolutions.pmt.domain.User;
import com.codesolutions.pmt.dto.MemberDto;
import com.codesolutions.pmt.exception.ConflictException;
import com.codesolutions.pmt.exception.ForbiddenException;
import com.codesolutions.pmt.exception.NotFoundException;
import com.codesolutions.pmt.repository.ProjectMemberRepository;
import com.codesolutions.pmt.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestion des membres et des roles, et point central des controles de permission.
 * Le role est evalue par projet (cf. tableau des permissions de l'enonce).
 */
@Service
public class MembershipService {

    private final ProjectMemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public MembershipService(ProjectMemberRepository memberRepository,
                             ProjectRepository projectRepository,
                             UserService userService) {
        this.memberRepository = memberRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    // ---- Controles de permission reutilises par les autres services ----

    /** Verifie et retourne l'appartenance de l'utilisateur au projet, sinon 403. */
    @Transactional(readOnly = true)
    public ProjectMember requireMembership(Long projectId, Long userId) {
        return memberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ForbiddenException(
                        "Vous n'etes pas membre de ce projet."));
    }

    /** Exige que l'utilisateur ait l'un des roles autorises sur le projet. */
    @Transactional(readOnly = true)
    public ProjectMember requireRole(Long projectId, Long userId, Role... allowed) {
        ProjectMember member = requireMembership(projectId, userId);
        for (Role r : allowed) {
            if (member.getRole() == r) {
                return member;
            }
        }
        throw new ForbiddenException("Action non autorisee pour votre role (" + member.getRole() + ").");
    }

    public Role roleOf(Long projectId, Long userId) {
        return requireMembership(projectId, userId).getRole();
    }

    // ---- Operations metier ----

    /** US: inviter un membre par e-mail et lui attribuer un role (admin uniquement). */
    @Transactional
    public MemberDto invite(Long projectId, Long actingUserId, String email, Role role) {
        requireRole(projectId, actingUserId, Role.ADMIN);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Projet introuvable: " + projectId));
        User invited = userService.getByEmail(email);
        if (memberRepository.existsByProjectIdAndUserId(projectId, invited.getId())) {
            throw new ConflictException("Cet utilisateur est deja membre du projet.");
        }
        ProjectMember member = memberRepository.save(new ProjectMember(project, invited, role));
        return DtoMapper.toMemberDto(member);
    }

    /** US: attribuer / changer le role d'un membre (admin uniquement). */
    @Transactional
    public MemberDto changeRole(Long projectId, Long actingUserId, Long memberId, Role role) {
        requireRole(projectId, actingUserId, Role.ADMIN);
        ProjectMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Membre introuvable: " + memberId));
        if (!member.getProject().getId().equals(projectId)) {
            throw new NotFoundException("Ce membre n'appartient pas au projet indique.");
        }
        member.setRole(role);
        return DtoMapper.toMemberDto(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public List<MemberDto> listMembers(Long projectId, Long actingUserId) {
        requireMembership(projectId, actingUserId);
        return memberRepository.findByProjectId(projectId).stream()
                .map(DtoMapper::toMemberDto)
                .toList();
    }
}
