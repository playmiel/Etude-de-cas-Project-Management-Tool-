import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { User } from '../models';
import { API_BASE_URL } from '../environment';

const STORAGE_KEY = 'pmt_user';

/** Gere l'inscription, la connexion et l'utilisateur courant (sans Spring Security). */
@Injectable({ providedIn: 'root' })
export class AuthService {
  /** Utilisateur courant expose en signal pour les composants. */
  readonly currentUser = signal<User | null>(this.restore());

  constructor(private http: HttpClient) {}

  register(username: string, email: string, password: string): Observable<User> {
    return this.http.post<User>(`${API_BASE_URL}/auth/register`, { username, email, password });
  }

  login(email: string, password: string): Observable<User> {
    return this.http.post<User>(`${API_BASE_URL}/auth/login`, { email, password })
      .pipe(tap((user) => this.setUser(user)));
  }

  logout(): void {
    this.currentUser.set(null);
    localStorage.removeItem(STORAGE_KEY);
  }

  isAuthenticated(): boolean {
    return this.currentUser() !== null;
  }

  userId(): number | null {
    return this.currentUser()?.id ?? null;
  }

  private setUser(user: User): void {
    this.currentUser.set(user);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
  }

  private restore(): User | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as User) : null;
  }
}
