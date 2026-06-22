package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.Project;
import com.codesolutions.pmt.domain.ProjectMember;
import com.codesolutions.pmt.domain.Role;
import com.codesolutions.pmt.domain.User;
import com.codesolutions.pmt.dto.ProjectDto;
import com.codesolutions.pmt.dto.ProjectRequest;
import com.codesolutions.pmt.exception.NotFoundException;
import com.codesolutions.pmt.repository.ProjectMemberRepository;
import com.codesolutions.pmt.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Creation et consultation des projets. */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final MembershipService membershipService;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository memberRepository,
                          MembershipService membershipService,
                          UserService userService) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.membershipService = membershipService;
        this.userService = userService;
    }

    /** US: creer un projet -> le createur en devient administrateur. */
    @Transactional
    public ProjectDto create(Long actingUserId, ProjectRequest request) {
        User creator = userService.getById(actingUserId);
        Project project = projectRepository.save(
                new Project(request.name(), request.description(), request.startDate()));
        memberRepository.save(new ProjectMember(project, creator, Role.ADMIN));
        return DtoMapper.toProjectDto(project, Role.ADMIN);
    }

    /** Liste des projets auxquels l'utilisateur appartient, avec son role. */
    @Transactional(readOnly = true)
    public List<ProjectDto> listForUser(Long actingUserId) {
        return memberRepository.findByUserId(actingUserId).stream()
                .map(m -> DtoMapper.toProjectDto(m.getProject(), m.getRole()))
                .toList();
    }

    /** Detail d'un projet (reserve aux membres). */
    @Transactional(readOnly = true)
    public ProjectDto getById(Long projectId, Long actingUserId) {
        Role role = membershipService.roleOf(projectId, actingUserId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Projet introuvable: " + projectId));
        return DtoMapper.toProjectDto(project, role);
    }
}
