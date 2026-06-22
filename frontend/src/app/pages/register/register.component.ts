import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="card">
      <h2>Inscription</h2>
      <div class="col">
        <label>Nom d'utilisateur</label>
        <input name="username" [(ngModel)]="username" />
      </div>
      <div class="col">
        <label>E-mail</label>
        <input type="email" name="email" [(ngModel)]="email" />
      </div>
      <div class="col">
        <label>Mot de passe</label>
        <input type="password" name="password" [(ngModel)]="password" />
      </div>
      @if (error) { <p class="error">{{ error }}</p> }
      @if (success) { <p>Compte cree. <a routerLink="/login">Se connecter</a></p> }
      <p>
        <button (click)="submit()" [disabled]="!username || !email || !password">S'inscrire</button>
      </p>
    </div>
  `
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  error = '';
  success = false;

  constructor(private auth: AuthService, private router: Router) {}

  submit(): void {
    this.error = '';
    this.auth.register(this.username, this.email, this.password).subscribe({
      next: () => {
        this.success = true;
        this.router.navigate(['/login']);
      },
      error: (err) => (this.error = err?.error?.message ?? 'Inscription impossible.')
    });
  }
}
