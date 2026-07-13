import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  user: { id: string; email: string; displayName: string; roles: string[] };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'access_token';
  readonly token = signal<string | null>(this.getToken());

  constructor(private http: HttpClient) {}

  register(email: string, displayName: string, password: string) {
    return this.http
      .post<AuthResponse>('/api/auth/register', { email, displayName, password })
      .pipe(tap((r) => this.setToken(r.accessToken)));
  }

  login(email: string, password: string) {
    return this.http
      .post<AuthResponse>('/api/auth/login', { email, password })
      .pipe(tap((r) => this.setToken(r.accessToken)));
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.token.set(null);
  }

  getToken(): string | null {
    const v = localStorage.getItem(this.tokenKey);
    return v && v.trim().length ? v : null;
  }

  private setToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
    this.token.set(token);
  }
}

