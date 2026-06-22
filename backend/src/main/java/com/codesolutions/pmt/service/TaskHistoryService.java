package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.Task;
import com.codesolutions.pmt.domain.TaskHistory;
import com.codesolutions.pmt.domain.User;
import com.codesolutions.pmt.dto.TaskHistoryDto;
import com.codesolutions.pmt.repository.TaskHistoryRepository;
import com.codesolutions.pmt.repository.TaskRepository;
import com.codesolutions.pmt.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Journalisation et consultation de l'historique des modifications des taches. */
@Service
public class TaskHistoryService {

    private final TaskHistoryRepository historyRepository;
    private final TaskRepository taskRepository;
    private final MembershipService membershipService;

    public TaskHistoryService(TaskHistoryRepository historyRepository,
                              TaskRepository taskRepository,
                              MembershipService membershipService) {
        this.historyRepository = historyRepository;
        this.taskRepository = taskRepository;
        this.membershipService = membershipService;
    }

    @Transactional
    public void record(Task task, User author, String description) {
        historyRepository.save(new TaskHistory(task, author, description));
    }

    /** US: tous les roles peuvent suivre l'historique des modifications. */
    @Transactional(readOnly = true)
    public List<TaskHistoryDto> listForTask(Long taskId, Long actingUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Tache introuvable: " + taskId));
        membershipService.requireMembership(task.getProject().getId(), actingUserId);
        return historyRepository.findByTaskIdOrderByChangedAtDesc(taskId).stream()
                .map(DtoMapper::toHistoryDto)
                .toList();
    }
}
