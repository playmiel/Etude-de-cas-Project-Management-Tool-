import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardColumn, Priority, Task, TaskHistory, TaskStatus } from '../models';
import { API_BASE_URL } from '../environment';

export interface TaskCreate {
  name: string;
  description?: string | null;
  dueDate?: string | null;
  priority?: Priority | null;
  assigneeId?: number | null;
}

export interface TaskUpdate {
  name?: string | null;
  description?: string | null;
  dueDate?: string | null;
  endDate?: string | null;
  priority?: Priority | null;
  status?: TaskStatus | null;
  assigneeId?: number | null;
}

/** Appels REST relatifs aux taches, au tableau de bord et a l'historique. */
@Injectable({ providedIn: 'root' })
export class TaskService {
  constructor(private http: HttpClient) {}

  list(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${API_BASE_URL}/projects/${projectId}/tasks`);
  }

  dashboard(projectId: number): Observable<DashboardColumn[]> {
    return this.http.get<DashboardColumn[]>(`${API_BASE_URL}/projects/${projectId}/dashboard`);
  }

  create(projectId: number, task: TaskCreate): Observable<Task> {
    return this.http.post<Task>(`${API_BASE_URL}/projects/${projectId}/tasks`, task);
  }

  get(taskId: number): Observable<Task> {
    return this.http.get<Task>(`${API_BASE_URL}/tasks/${taskId}`);
  }

  update(taskId: number, update: TaskUpdate): Observable<Task> {
    return this.http.put<Task>(`${API_BASE_URL}/tasks/${taskId}`, update);
  }

  assign(taskId: number, assigneeId: number | null): Observable<Task> {
    return this.http.patch<Task>(`${API_BASE_URL}/tasks/${taskId}/assignee`, { assigneeId });
  }

  history(taskId: number): Observable<TaskHistory[]> {
    return this.http.get<TaskHistory[]>(`${API_BASE_URL}/tasks/${taskId}/history`);
  }
}
