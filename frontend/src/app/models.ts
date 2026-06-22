// Modeles partages cote frontend (alignes sur les DTO du backend).

export type Role = 'ADMIN' | 'MEMBER' | 'OBSERVER';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH';
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface User {
  id: number;
  username: string;
  email: string;
}

export interface Project {
  id: number;
  name: string;
  description?: string;
  startDate?: string;
  currentUserRole: Role;
}

export interface Member {
  memberId: number;
  userId: number;
  username: string;
  email: string;
  role: Role;
}

export interface Task {
  id: number;
  projectId: number;
  name: string;
  description?: string;
  dueDate?: string;
  endDate?: string;
  priority: Priority;
  status: TaskStatus;
  assignee?: User;
}

export interface DashboardColumn {
  status: TaskStatus;
  tasks: Task[];
}

export interface TaskHistory {
  id: number;
  changeDescription: string;
  changedByUsername: string;
  changedAt: string;
}

export interface Notification {
  id: number;
  message: string;
  read: boolean;
  createdAt: string;
  taskId?: number;
}
