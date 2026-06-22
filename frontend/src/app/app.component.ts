import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <nav>
      <a routerLink="/projects"><strong>PMT</strong></a>
      <span class="spacer"></span>
      @if (auth.currentUser(); as user) {
        <span>{{ user.username }}</span>
        <button class="secondary" (click)="auth.logout()">Deconnexion</button>
      } @else {
        <a routerLink="/login">Connexion</a>
        <a routerLink="/register">Inscription</a>
      }
    </nav>
    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {
  constructor(public auth: AuthService) {}
}
