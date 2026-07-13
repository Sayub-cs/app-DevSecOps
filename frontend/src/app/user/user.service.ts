import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id: string;
  email: string;
  displayName: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<User> {
    return this.http.get<User>('/api/users/me');
  }

  updateCurrentUser(data: { email: string; displayName: string }): Observable<User> {
    return this.http.put<User>('/api/users/me', data);
  }
}