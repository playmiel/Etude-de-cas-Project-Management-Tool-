package com.codesolutions.pmt.web;

import com.codesolutions.pmt.dto.*;
import com.codesolutions.pmt.service.TaskHistoryService;
import com.codesolutions.pmt.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Gestion des taches, du tableau de bord et de l'historique. */
@RestController
@RequestMapping("/api")
@Tag(name = "Taches")
public class TaskController {

    private final TaskService taskService;
    private final TaskHistoryService historyService;

    public TaskController(TaskService taskService, TaskHistoryService historyService) {
        this.taskService = taskService;
        this.historyService = historyService;
    }

    @Operation(summary = "Creer une tache dans un projet (admin/membre)")
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskDto> create(@RequestHeader("X-User-Id") Long userId,
                                          @PathVariable Long projectId,
                                          @Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(projectId, userId, request));
    }

    @Operation(summary = "Lister les taches d'un projet")
    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskDto> list(@RequestHeader("X-User-Id") Long userId, @PathVariable Long projectId) {
        return taskService.listByProject(projectId, userId);
    }

    @Operation(summary = "Tableau de bord : taches regroupees par statut")
    @GetMapping("/projects/{projectId}/dashboard")
    public List<DashboardColumnDto> dashboard(@RequestHeader("X-User-Id") Long userId,
                                              @PathVariable Long projectId) {
        return taskService.dashboard(projectId, userId);
    }

    @Operation(summary = "Visualiser une tache unitaire (tous les roles)")
    @GetMapping("/tasks/{taskId}")
    public TaskDto getOne(@RequestHeader("X-User-Id") Long userId, @PathVariable Long taskId) {
        return taskService.getOne(taskId, userId);
    }

    @Operation(summary = "Mettre a jour une tache (admin/membre)")
    @PutMapping("/tasks/{taskId}")
    public TaskDto update(@RequestHeader("X-User-Id") Long userId,
                          @PathVariable Long taskId,
                          @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.update(taskId, userId, request);
    }

    @Operation(summary = "Assigner une tache a un membre (admin/membre)")
    @PatchMapping("/tasks/{taskId}/assignee")
    public TaskDto assign(@RequestHeader("X-User-Id") Long userId,
                          @PathVariable Long taskId,
                          @Valid @RequestBody AssignTaskRequest request) {
        return taskService.assign(taskId, userId, request.assigneeId());
    }

    @Operation(summary = "Historique des modifications d'une tache (tous les roles)")
    @GetMapping("/tasks/{taskId}/history")
    public List<TaskHistoryDto> history(@RequestHeader("X-User-Id") Long userId,
                                        @PathVariable Long taskId) {
        return historyService.listForTask(taskId, userId);
    }
}
