package com.codesolutions.pmt.web;

import com.codesolutions.pmt.dto.ProjectDto;
import com.codesolutions.pmt.dto.ProjectRequest;
import com.codesolutions.pmt.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Gestion des projets. L'utilisateur courant est identifie par l'en-tete X-User-Id. */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projets")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Creer un projet (le createur devient administrateur)")
    @PostMapping
    public ResponseEntity<ProjectDto> create(@RequestHeader("X-User-Id") Long userId,
                                             @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(userId, request));
    }

    @Operation(summary = "Lister les projets de l'utilisateur courant")
    @GetMapping
    public List<ProjectDto> list(@RequestHeader("X-User-Id") Long userId) {
        return projectService.listForUser(userId);
    }

    @Operation(summary = "Detail d'un projet")
    @GetMapping("/{projectId}")
    public ProjectDto get(@RequestHeader("X-User-Id") Long userId, @PathVariable Long projectId) {
        return projectService.getById(projectId, userId);
    }
}
