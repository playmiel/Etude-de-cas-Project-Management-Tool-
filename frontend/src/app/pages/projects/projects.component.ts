import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Project } from '../../models';
import { ProjectService } from '../../core/project.service';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <h2>Mes projets</h2>
    <div class="card">
      <h3>Nouveau projet</h3>
      <div class="row">
        <div class="col"><input placeholder="Nom" name="name" [(ngModel)]="name" /></div>
        <div class="col"><input placeholder="Description" name="desc" [(ngModel)]="description" /></div>
        <div class="col"><input type="date" name="start" [(ngModel)]="startDate" /></div>
      </div>
      <p><button (click)="create()" [disabled]="!name">Creer</button></p>
    </div>

    @for (project of projects; track project.id) {
      <div class="card">
        <a [routerLink]="['/projects', project.id]"><strong>{{ project.name }}</strong></a>
        <span class="badge">{{ project.currentUserRole }}</span>
        <p>{{ project.description }}</p>
      </div>
    } @empty {
      <p>Aucun projet pour le moment.</p>
    }
  `
})
export class ProjectsComponent implements OnInit {
  projects: Project[] = [];
  name = '';
  description = '';
  startDate = '';

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.projectService.list().subscribe((p) => (this.projects = p));
  }

  create(): void {
    this.projectService.create(this.name, this.description, this.startDate || null).subscribe(() => {
      this.name = '';
      this.description = '';
      this.startDate = '';
      this.reload();
    });
  }
}
