import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'projects' },
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent) },
  { path: 'projects', canActivate: [authGuard], loadComponent: () => import('./pages/projects/projects.component').then(m => m.ProjectsComponent) },
  { path: 'projects/:id', canActivate: [authGuard], loadComponent: () => import('./pages/project-detail/project-detail.component').then(m => m.ProjectDetailComponent) },
  { path: 'projects/:id/dashboard', canActivate: [authGuard], loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'tasks/:id', canActivate: [authGuard], loadComponent: () => import('./pages/task-detail/task-detail.component').then(m => m.TaskDetailComponent) },
  { path: '**', redirectTo: 'projects' }
];
