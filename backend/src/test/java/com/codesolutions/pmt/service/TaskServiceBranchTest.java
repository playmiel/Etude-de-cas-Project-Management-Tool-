package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.Priority;
import com.codesolutions.pmt.domain.Role;
import com.codesolutions.pmt.domain.TaskStatus;
import com.codesolutions.pmt.dto.*;
import com.codesolutions.pmt.exception.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Couvre en profondeur les branches de TaskService (mise a jour partielle, assignation). */
@SpringBootTest
@ActiveProfiles("test")
class TaskServiceBranchTest {

    @Autowired AuthService authService;
    @Autowired ProjectService projectService;
    @Autowired MembershipService membershipService;
    @Autowired TaskService taskService;

    private long admin;
    private long member;
    private long outsider;
    private long projectId;

    @BeforeEach
    void setUp() {
        admin = authService.register(new RegisterRequest("adm_" + uid(), email(), "secret123")).id();
        UserDto m = authService.register(new RegisterRequest("mem_" + uid(), email(), "secret123"));
        member = m.id();
        outsider = authService.register(new RegisterRequest("out_" + uid(), email(), "secret123")).id();
        projectId = projectService.create(admin, new ProjectRequest("P", "d", LocalDate.now())).id();
        membershipService.invite(projectId, admin, emailOf(member), Role.MEMBER);
    }

    @Test
    void update_touchesEveryFieldBranch() {
        TaskDto task = taskService.create(projectId, admin,
                new TaskCreateRequest("T", "desc", LocalDate.parse("2026-03-01"), Priority.LOW, null));

        // Toutes les branches "vraies" (valeur differente)
        TaskDto updated = taskService.update(task.id(), admin, new TaskUpdateRequest(
                "T2", "desc2", LocalDate.parse("2026-04-01"), LocalDate.parse("2026-04-10"),
                Priority.HIGH, TaskStatus.IN_PROGRESS, member));
        assertThat(updated.name()).isEqualTo("T2");
        assertThat(updated.assignee().id()).isEqualTo(member);
        assertThat(updated.status()).isEqualTo(TaskStatus.IN_PROGRESS);

        // Toutes les branches "fausses" (memes valeurs) + assignation identique -> aucun changement
        TaskDto noop = taskService.update(task.id(), admin, new TaskUpdateRequest(
                "T2", "desc2", LocalDate.parse("2026-04-01"), LocalDate.parse("2026-04-10"),
                Priority.HIGH, TaskStatus.IN_PROGRESS, member));
        assertThat(noop.name()).isEqualTo("T2");

        // Update totalement vide -> aucune branche, historique inchange
        TaskDto empty = taskService.update(task.id(), admin, new TaskUpdateRequest(
                null, null, null, null, null, null, null));
        assertThat(empty.id()).isEqualTo(task.id());
    }

    @Test
    void assign_andUnassignBranches() {
        TaskDto task = taskService.create(projectId, member,
                new TaskCreateRequest("A", null, null, null, member));
        // Re-assignation a une autre personne
        TaskDto reassigned = taskService.assign(task.id(), admin, admin);
        assertThat(reassigned.assignee().id()).isEqualTo(admin);
        // Desassignation (assigneeId null -> branche "personne")
        TaskDto unassigned = taskService.assign(task.id(), admin, null);
        assertThat(unassigned.assignee()).isNull();
    }

    @Test
    void assign_toNonMemberIsForbidden() {
        TaskDto task = taskService.create(projectId, admin,
                new TaskCreateRequest("X", null, null, Priority.MEDIUM, null));
        assertThatThrownBy(() -> taskService.assign(task.id(), admin, outsider))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void listAndDashboardReflectStatuses() {
        taskService.create(projectId, admin, new TaskCreateRequest("t1", null, null, null, null));
        taskService.create(projectId, admin, new TaskCreateRequest("t2", null, null, null, null));
        assertThat(taskService.listByProject(projectId, admin)).hasSize(2);
        assertThat(taskService.dashboard(projectId, admin)).hasSize(3); // 3 statuts
    }

    // --- helpers ---
    private static int counter = 0;
    private static synchronized int next() { return ++counter; }
    private int currentUid;
    private int uid() { currentUid = next(); return currentUid; }
    private String email() { return "u" + currentUid + "@pmt.local"; }
    private String emailOf(long userId) {
        // les emails sont generes sequentiellement; on retrouve via le service utilisateur
        return membershipResolveEmail(userId);
    }
    @Autowired UserService userService;
    private String membershipResolveEmail(long userId) {
        return userService.getById(userId).getEmail();
    }
}
