import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification } from '../models';
import { API_BASE_URL } from '../environment';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private http: HttpClient) {}

  list(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${API_BASE_URL}/notifications`);
  }
}
