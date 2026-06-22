import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Member, Project, Role } from '../models';
import { API_BASE_URL } from '../environment';

/** Appels REST relatifs aux projets et a leurs membres. */
@Injectable({ providedIn: 'root' })
export class ProjectService {
  constructor(private http: HttpClient) {}

  list(): Observable<Project[]> {
    return this.http.get<Project[]>(`${API_BASE_URL}/projects`);
  }

  get(projectId: number): Observable<Project> {
    return this.http.get<Project>(`${API_BASE_URL}/projects/${projectId}`);
  }

  create(name: string, description: string, startDate: string | null): Observable<Project> {
    return this.http.post<Project>(`${API_BASE_URL}/projects`, { name, description, startDate });
  }

  members(projectId: number): Observable<Member[]> {
    return this.http.get<Member[]>(`${API_BASE_URL}/projects/${projectId}/members`);
  }

  invite(projectId: number, email: string, role: Role): Observable<Member> {
    return this.http.post<Member>(`${API_BASE_URL}/projects/${projectId}/members`, { email, role });
  }

  changeRole(projectId: number, memberId: number, role: Role): Observable<Member> {
    return this.http.put<Member>(`${API_BASE_URL}/projects/${projectId}/members/${memberId}/role`, { role });
  }
}
