import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="card">
      <h2>Connexion</h2>
      <div class="col">
        <label>E-mail</label>
        <input type="email" name="email" [(ngModel)]="email" />
      </div>
      <div class="col">
        <label>Mot de passe</label>
        <input type="password" name="password" [(ngModel)]="password" />
      </div>
      @if (error) { <p class="error">{{ error }}</p> }
      <p>
        <button (click)="submit()" [disabled]="!email || !password">Se connecter</button>
      </p>
      <p>Pas de compte ? <a routerLink="/register">Inscription</a></p>
    </div>
  `
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  submit(): void {
    this.error = '';
    this.auth.login(this.email, this.password).subscribe({
      next: () => this.router.navigate(['/projects']),
      error: () => (this.error = 'Identifiants invalides.')
    });
  }
}
