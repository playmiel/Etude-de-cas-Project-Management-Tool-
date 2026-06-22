package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.*;
import com.codesolutions.pmt.dto.*;
import com.codesolutions.pmt.exception.NotFoundException;
import com.codesolutions.pmt.repository.ProjectRepository;
import com.codesolutions.pmt.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Cycle de vie des taches : creation, mise a jour, assignation, consultation et tableau de bord.
 * Applique les permissions par role, journalise l'historique et declenche les notifications.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final MembershipService membershipService;
    private final TaskHistoryService historyService;
    private final NotificationService notificationService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       MembershipService membershipService,
                       TaskHistoryService historyService,
                       NotificationService notificationService,
                       UserService userService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.membershipService = membershipService;
        this.historyService = historyService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    /** US: creer une tache (ADMIN ou MEMBRE). Une assignation est possible des la creation. */
    @Transactional
    public TaskDto create(Long projectId, Long actingUserId, TaskCreateRequest request) {
        User actor = membershipService.requireRole(projectId, actingUserId, Role.ADMIN, Role.MEMBER).getUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Projet introuvable: " + projectId));

        Task task = new Task();
        task.setProject(project);
        task.setName(request.name());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority() != null ? request.priority() : Priority.MEDIUM);
        task.setStatus(TaskStatus.TODO);

        User assignee = resolveAssignee(projectId, request.assigneeId());
        task.setAssignee(assignee);

        Task saved = taskRepository.save(task);
        historyService.record(saved, actor, "Tache creee : '" + saved.getName() + "'");
        if (assignee != null) {
            notificationService.notifyAssignment(saved, assignee);
        }
        return DtoMapper.toTaskDto(saved);
    }

    /** US: assigner une tache a un membre specifique (ADMIN ou MEMBRE). */
    @Transactional
    public TaskDto assign(Long taskId, Long actingUserId, Long assigneeId) {
        Task task = getTaskEntity(taskId);
        User actor = membershipService.requireRole(task.getProject().getId(), actingUserId,
                Role.ADMIN, Role.MEMBER).getUser();
        User assignee = resolveAssignee(task.getProject().getId(), assigneeId);
        task.setAssignee(assignee);
        task.setUpdatedAt(Instant.now());
        Task saved = taskRepository.save(task);
        String who = assignee != null ? assignee.getUsername() : "personne";
        historyService.record(saved, actor, "Assignation modifiee -> " + who);
        if (assignee != null) {
            notificationService.notifyAssignment(saved, assignee);
        }
        return DtoMapper.toTaskDto(saved);
    }

    /** US: mettre a jour n'importe quelle information d'une tache ou ajouter une date de fin (ADMIN/MEMBRE). */
    @Transactional
    public TaskDto update(Long taskId, Long actingUserId, TaskUpdateRequest request) {
        Task task = getTaskEntity(taskId);
        User actor = membershipService.requireRole(task.getProject().getId(), actingUserId,
                Role.ADMIN, Role.MEMBER).getUser();

        List<String> changes = new ArrayList<>();
        boolean newAssignment = false;

        if (request.name() != null && !request.name().equals(task.getName())) {
            changes.add("nom: '" + task.getName() + "' -> '" + request.name() + "'");
            task.setName(request.name());
        }
        if (request.description() != null && !request.description().equals(task.getDescription())) {
            changes.add("description modifiee");
            task.setDescription(request.description());
        }
        if (request.dueDate() != null && !request.dueDate().equals(task.getDueDate())) {
            changes.add("echeance -> " + request.dueDate());
            task.setDueDate(request.dueDate());
        }
        if (request.endDate() != null && !request.endDate().equals(task.getEndDate())) {
            changes.add("date de fin -> " + request.endDate());
            task.setEndDate(request.endDate());
        }
        if (request.priority() != null && request.priority() != task.getPriority()) {
            changes.add("priorite: " + task.getPriority() + " -> " + request.priority());
            task.setPriority(request.priority());
        }
        if (request.status() != null && request.status() != task.getStatus()) {
            changes.add("statut: " + task.getStatus() + " -> " + request.status());
            task.setStatus(request.status());
        }
        if (request.assigneeId() != null) {
            User assignee = resolveAssignee(task.getProject().getId(), request.assigneeId());
            Long previous = task.getAssignee() != null ? task.getAssignee().getId() : null;
            if (assignee != null && !assignee.getId().equals(previous)) {
                task.setAssignee(assignee);
                changes.add("assignation -> " + assignee.getUsername());
                newAssignment = true;
            }
        }

        task.setUpdatedAt(Instant.now());
        Task saved = taskRepository.save(task);

        if (!changes.isEmpty()) {
            historyService.record(saved, actor, String.join(" ; ", changes));
        }
        if (newAssignment) {
            notificationService.notifyAssignment(saved, saved.getAssignee());
        }
        return DtoMapper.toTaskDto(saved);
    }

    /** US: visualiser une tache unitaire (tous les roles). */
    @Transactional(readOnly = true)
    public TaskDto getOne(Long taskId, Long actingUserId) {
        Task task = getTaskEntity(taskId);
        membershipService.requireMembership(task.getProject().getId(), actingUserId);
        return DtoMapper.toTaskDto(task);
    }

    /** Liste a plat des taches d'un projet (tous les roles). */
    @Transactional(readOnly = true)
    public List<TaskDto> listByProject(Long projectId, Long actingUserId) {
        membershipService.requireMembership(projectId, actingUserId);
        return taskRepository.findByProjectId(projectId).stream()
                .map(DtoMapper::toTaskDto)
                .toList();
    }

    /** US: tableau de bord = taches regroupees par statut (tous les roles). */
    @Transactional(readOnly = true)
    public List<DashboardColumnDto> dashboard(Long projectId, Long actingUserId) {
        membershipService.requireMembership(projectId, actingUserId);
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        List<DashboardColumnDto> columns = new ArrayList<>();
        for (TaskStatus status : TaskStatus.values()) {
            List<TaskDto> column = tasks.stream()
                    .filter(t -> t.getStatus() == status)
                    .map(DtoMapper::toTaskDto)
                    .toList();
            columns.add(new DashboardColumnDto(status, column));
        }
        return columns;
    }

    // ---- Helpers ----

    private Task getTaskEntity(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Tache introuvable: " + taskId));
    }

    /** L'assigne doit etre membre du projet ; null = desassignation. */
    private User resolveAssignee(Long projectId, Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        membershipService.requireMembership(projectId, assigneeId);
        return userService.getById(assigneeId);
    }
}
