package com.codesolutions.pmt.web;

import com.codesolutions.pmt.domain.Priority;
import com.codesolutions.pmt.domain.Role;
import com.codesolutions.pmt.domain.TaskStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test d'integration de bout en bout : exerce les controleurs, services,
 * repositories et la gestion des erreurs sur une base H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PmtIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    private long register(String username, String email, String password) throws Exception {
        MvcResult res = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("username", username, "email", email, "password", password))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andReturn();
        return json.readTree(res.getResponse().getContentAsString()).get("id").asLong();
    }

    private long createProject(long userId, String name) throws Exception {
        MvcResult res = mvc.perform(post("/api/projects")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("name", name, "description", "desc", "startDate", "2026-01-10"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currentUserRole").value("ADMIN"))
                .andReturn();
        return json.readTree(res.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void fullCollaborationFlow() throws Exception {
        long admin = register("alice", "alice@pmt.local", "secret123");
        long member = register("bob", "bob@pmt.local", "secret123");
        long observer = register("carol", "carol@pmt.local", "secret123");

        // Connexion
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("email", "alice@pmt.local", "password", "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));

        long projectId = createProject(admin, "PMT");

        // Invitation de membres avec roles
        mvc.perform(post("/api/projects/{p}/members", projectId)
                        .header("X-User-Id", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", "bob@pmt.local", "role", "MEMBER"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("MEMBER"));

        MvcResult obsRes = mvc.perform(post("/api/projects/{p}/members", projectId)
                        .header("X-User-Id", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", "carol@pmt.local", "role", "OBSERVER"))))
                .andExpect(status().isCreated())
                .andReturn();
        long observerMemberId = json.readTree(obsRes.getResponse().getContentAsString()).get("memberId").asLong();

        // Liste des membres -> 3
        mvc.perform(get("/api/projects/{p}/members", projectId).header("X-User-Id", member))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        // Changement de role (observer -> member) par l'admin
        mvc.perform(put("/api/projects/{p}/members/{m}/role", projectId, observerMemberId)
                        .header("X-User-Id", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("role", "OBSERVER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("OBSERVER"));

        // Creation d'une tache par le membre, assignee a l'admin
        MvcResult taskRes = mvc.perform(post("/api/projects/{p}/tasks", projectId)
                        .header("X-User-Id", member)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "name", "Implementer login",
                                "description", "ecran de connexion",
                                "dueDate", "2026-02-01",
                                "priority", Priority.HIGH.name(),
                                "assigneeId", admin))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.assignee.username").value("alice"))
                .andReturn();
        long taskId = json.readTree(taskRes.getResponse().getContentAsString()).get("id").asLong();

        // Visualisation unitaire (observer autorise)
        mvc.perform(get("/api/tasks/{t}", taskId).header("X-User-Id", observer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Implementer login"));

        // Mise a jour (changement de statut + date de fin) par l'admin
        mvc.perform(put("/api/tasks/{t}", taskId)
                        .header("X-User-Id", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "status", TaskStatus.DONE.name(),
                                "endDate", "2026-01-20",
                                "priority", Priority.LOW.name()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.endDate").value("2026-01-20"));

        // Re-assignation via PATCH
        mvc.perform(patch("/api/tasks/{t}/assignee", taskId)
                        .header("X-User-Id", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("assigneeId", member))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignee.username").value("bob"));

        // Tableau de bord : 3 colonnes, la tache est en DONE
        mvc.perform(get("/api/projects/{p}/dashboard", projectId).header("X-User-Id", observer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        // Liste a plat des taches
        mvc.perform(get("/api/projects/{p}/tasks", projectId).header("X-User-Id", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Historique : creation + maj + reassignation
        mvc.perform(get("/api/tasks/{t}/history", taskId).header("X-User-Id", observer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));

        // Notifications de l'assigne courant (bob)
        mvc.perform(get("/api/notifications").header("X-User-Id", member))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // Liste des projets de l'admin
        mvc.perform(get("/api/projects").header("X-User-Id", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Detail du projet
        mvc.perform(get("/api/projects/{p}", projectId).header("X-User-Id", member))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PMT"));
    }

    @Test
    void permissionsAndErrors() throws Exception {
        long admin = register("dave", "dave@pmt.local", "secret123");
        long outsider = register("eve", "eve@pmt.local", "secret123");
        long projectId = createProject(admin, "Secured");

        // Un non-membre ne peut pas voir le projet -> 403
        mvc.perform(get("/api/projects/{p}", projectId).header("X-User-Id", outsider))
                .andExpect(status().isForbidden());

        // Un non-admin (non-membre ici) ne peut pas inviter -> 403
        mvc.perform(post("/api/projects/{p}/members", projectId)
                        .header("X-User-Id", outsider)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", "eve@pmt.local", "role", "MEMBER"))))
                .andExpect(status().isForbidden());

        // Inscription en doublon -> 409
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "username", "dave2", "email", "dave@pmt.local", "password", "secret123"))))
                .andExpect(status().isConflict());

        // Mauvais mot de passe -> 401
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "email", "dave@pmt.local", "password", "wrong"))))
                .andExpect(status().isUnauthorized());

        // Tache inexistante -> 404
        mvc.perform(get("/api/tasks/{t}", 999999).header("X-User-Id", admin))
                .andExpect(status().isNotFound());

        // Validation : email invalide -> 400 avec fieldErrors
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "username", "x", "email", "not-an-email", "password", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists());

        // Un observateur ne peut pas creer de tache -> 403
        long observer = register("frank", "frank@pmt.local", "secret123");
        mvc.perform(post("/api/projects/{p}/members", projectId)
                        .header("X-User-Id", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("email", "frank@pmt.local", "role", "OBSERVER"))))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/projects/{p}/tasks", projectId)
                        .header("X-User-Id", observer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("name", "interdit"))))
                .andExpect(status().isForbidden());
    }
}
