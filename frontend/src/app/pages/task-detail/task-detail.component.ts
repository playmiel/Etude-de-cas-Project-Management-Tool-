import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Task, TaskHistory, TaskStatus, Priority } from '../../models';
import { TaskService } from '../../core/task.service';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [FormsModule],
  template: `
    @if (task) {
      <h2>{{ task.name }}</h2>
      <div class="card">
        <div class="col">
          <label>Description</label>
          <textarea name="description" [(ngModel)]="task.description"></textarea>
        </div>
        <div class="row">
          <div class="col">
            <label>Statut</label>
            <select name="status" [(ngModel)]="task.status">
              <option value="TODO">A faire</option>
              <option value="IN_PROGRESS">En cours</option>
              <option value="DONE">Termine</option>
            </select>
          </div>
          <div class="col">
            <label>Priorite</label>
            <select name="priority" [(ngModel)]="task.priority">
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
            </select>
          </div>
          <div class="col">
            <label>Date de fin</label>
            <input type="date" name="endDate" [(ngModel)]="task.endDate" />
          </div>
        </div>
        <p><button (click)="save()">Enregistrer</button></p>
        @if (saved) { <p>Modifications enregistrees.</p> }
      </div>

      <div class="card">
        <h3>Historique</h3>
        <table>
          <tr><th>Quand</th><th>Auteur</th><th>Modification</th></tr>
          @for (h of history; track h.id) {
            <tr><td>{{ h.changedAt }}</td><td>{{ h.changedByUsername }}</td><td>{{ h.changeDescription }}</td></tr>
          }
        </table>
      </div>
    }
  `
})
export class TaskDetailComponent implements OnInit {
  task?: Task;
  history: TaskHistory[] = [];
  saved = false;

  constructor(private route: ActivatedRoute, private taskService: TaskService) {}

  ngOnInit(): void {
    const taskId = Number(this.route.snapshot.paramMap.get('id'));
    this.taskService.get(taskId).subscribe((t) => (this.task = t));
    this.loadHistory(taskId);
  }

  loadHistory(taskId: number): void {
    this.taskService.history(taskId).subscribe((h) => (this.history = h));
  }

  save(): void {
    if (!this.task) {
      return;
    }
    this.saved = false;
    this.taskService.update(this.task.id, {
      description: this.task.description,
      status: this.task.status as TaskStatus,
      priority: this.task.priority as Priority,
      endDate: this.task.endDate
    }).subscribe((updated) => {
      this.task = updated;
      this.saved = true;
      this.loadHistory(updated.id);
    });
  }
}
