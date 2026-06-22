import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { DashboardColumn } from '../../models';
import { TaskService } from '../../core/task.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  template: `
    <h2>Tableau de bord</h2>
    <div class="columns">
      @for (col of columns; track col.status) {
        <div class="column">
          <h3>{{ label(col.status) }} ({{ col.tasks.length }})</h3>
          @for (t of col.tasks; track t.id) {
            <div class="card">
              <a [routerLink]="['/tasks', t.id]">{{ t.name }}</a>
              <div><span class="badge">{{ t.priority }}</span></div>
              <small>{{ t.assignee?.username || 'non assigne' }}</small>
            </div>
          } @empty {
            <p><small>Aucune tache</small></p>
          }
        </div>
      }
    </div>
  `
})
export class DashboardComponent implements OnInit {
  columns: DashboardColumn[] = [];

  constructor(private route: ActivatedRoute, private taskService: TaskService) {}

  ngOnInit(): void {
    const projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.taskService.dashboard(projectId).subscribe((c) => (this.columns = c));
  }

  label(status: string): string {
    switch (status) {
      case 'TODO': return 'A faire';
      case 'IN_PROGRESS': return 'En cours';
      case 'DONE': return 'Termine';
      default: return status;
    }
  }
}
