package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.*;
import com.codesolutions.pmt.dto.*;

/** Conversions entites -> DTO (centralisees pour eviter d'exposer les entites). */
public final class DtoMapper {

    private DtoMapper() {
    }

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public static ProjectDto toProjectDto(Project project, Role currentUserRole) {
        return new ProjectDto(project.getId(), project.getName(), project.getDescription(),
                project.getStartDate(), currentUserRole);
    }

    public static MemberDto toMemberDto(ProjectMember member) {
        User u = member.getUser();
        return new MemberDto(member.getId(), u.getId(), u.getUsername(), u.getEmail(), member.getRole());
    }

    public static TaskDto toTaskDto(Task task) {
        return new TaskDto(task.getId(), task.getProject().getId(), task.getName(), task.getDescription(),
                task.getDueDate(), task.getEndDate(), task.getPriority(), task.getStatus(),
                toUserDto(task.getAssignee()));
    }

    public static TaskHistoryDto toHistoryDto(TaskHistory history) {
        String author = history.getChangedBy() != null ? history.getChangedBy().getUsername() : "systeme";
        return new TaskHistoryDto(history.getId(), history.getChangeDescription(), author, history.getChangedAt());
    }

    public static NotificationDto toNotificationDto(Notification n) {
        Long taskId = n.getTask() != null ? n.getTask().getId() : null;
        return new NotificationDto(n.getId(), n.getMessage(), n.isRead(), n.getCreatedAt(), taskId);
    }
}
