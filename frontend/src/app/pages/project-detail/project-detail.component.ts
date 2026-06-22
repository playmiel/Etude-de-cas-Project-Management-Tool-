import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Member, Project, Role, Task, Priority } from '../../models';
import { ProjectService } from '../../core/project.service';
import { TaskService } from '../../core/task.service';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    @if (project) {
      <h2>{{ project.name }} <span class="badge">{{ project.currentUserRole }}</span></h2>
      <p>{{ project.description }}</p>
      <a [routerLink]="['/projects', project.id, 'dashboard']">Voir le tableau de bord</a>

      <div class="card">
        <h3>Membres</h3>
        <table>
          <tr><th>Utilisateur</th><th>E-mail</th><th>Role</th></tr>
          @for (m of members; track m.memberId) {
            <tr>
              <td>{{ m.username }}</td>
              <td>{{ m.email }}</td>
              <td>
                @if (canManage()) {
                  <select [ngModel]="m.role" (ngModelChange)="changeRole(m, $event)">
                    <option value="ADMIN">ADMIN</option>
                    <option value="MEMBER">MEMBER</option>
                    <option value="OBSERVER">OBSERVER</option>
                  </select>
                } @else {
                  <span class="badge">{{ m.role }}</span>
                }
              </td>
            </tr>
          }
        </table>
        @if (canManage()) {
          <div class="row" style="margin-top:.5rem">
            <div class="col"><input placeholder="E-mail a inviter" name="inviteEmail" [(ngModel)]="inviteEmail" /></div>
            <div class="col">
              <select name="inviteRole" [(ngModel)]="inviteRole">
                <option value="MEMBER">MEMBER</option>
                <option value="OBSERVER">OBSERVER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </div>
            <button (click)="invite()" [disabled]="!inviteEmail">Inviter</button>
          </div>
          @if (inviteError) { <p class="error">{{ inviteError }}</p> }
        }
      </div>

      <div class="card">
        <h3>Taches</h3>
        @if (canEdit()) {
          <div class="row">
            <div class="col"><input placeholder="Nom de la tache" name="taskName" [(ngModel)]="taskName" /></div>
            <div class="col">
              <select name="taskPriority" [(ngModel)]="taskPriority">
                <option value="LOW">LOW</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="HIGH">HIGH</option>
              </select>
            </div>
            <button (click)="createTask()" [disabled]="!taskName">Ajouter</button>
          </div>
        }
        <table>
          <tr><th>Nom</th><th>Statut</th><th>Priorite</th><th>Assigne</th></tr>
          @for (t of tasks; track t.id) {
            <tr>
              <td><a [routerLink]="['/tasks', t.id]">{{ t.name }}</a></td>
              <td>{{ t.status }}</td>
              <td>{{ t.priority }}</td>
              <td>{{ t.assignee?.username || '-' }}</td>
            </tr>
          }
        </table>
      </div>
    }
  `
})
export class ProjectDetailComponent implements OnInit {
  projectId!: number;
  project?: Project;
  members: Member[] = [];
  tasks: Task[] = [];

  inviteEmail = '';
  inviteRole: Role = 'MEMBER';
  inviteError = '';

  taskName = '';
  taskPriority: Priority = 'MEDIUM';

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.projectService.get(this.projectId).subscribe((p) => (this.project = p));
    this.loadMembers();
    this.loadTasks();
  }

  canManage(): boolean {
    return this.project?.currentUserRole === 'ADMIN';
  }

  canEdit(): boolean {
    return this.project?.currentUserRole === 'ADMIN' || this.project?.currentUserRole === 'MEMBER';
  }

  loadMembers(): void {
    this.projectService.members(this.projectId).subscribe((m) => (this.members = m));
  }

  loadTasks(): void {
    this.taskService.list(this.projectId).subscribe((t) => (this.tasks = t));
  }

  invite(): void {
    this.inviteError = '';
    this.projectService.invite(this.projectId, this.inviteEmail, this.inviteRole).subscribe({
      next: () => {
        this.inviteEmail = '';
        this.loadMembers();
      },
      error: (err) => (this.inviteError = err?.error?.message ?? 'Invitation impossible.')
    });
  }

  changeRole(member: Member, role: Role): void {
    this.projectService.changeRole(this.projectId, member.memberId, role).subscribe(() => this.loadMembers());
  }

  createTask(): void {
    this.taskService.create(this.projectId, { name: this.taskName, priority: this.taskPriority }).subscribe(() => {
      this.taskName = '';
      this.loadTasks();
    });
  }
}
