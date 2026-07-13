import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="card">
      <h2>Login</h2>
      <div class="row">
        <label>Email</label>
        <input [(ngModel)]="email" type="email" />
      </div>
      <div class="row">
        <label>Password</label>
        <input [(ngModel)]="password" type="password" />
      </div>

      <div class="actions">
        <button (click)="submit()" [disabled]="loading()">Login</button>
        <a routerLink="/register">Create account</a>
      </div>

      <p class="error" *ngIf="error()">{{ error() }}</p>
    </div>
  `,
  styles: [
    `
      .card {
        max-width: 420px;
        margin: 30px auto;
        padding: 18px;
        background: rgba(255, 255, 255, 0.04);
        border: 1px solid rgba(255, 255, 255, 0.08);
        border-radius: 12px;
      }
      .row {
        display: grid;
        gap: 6px;
        margin: 10px 0;
      }
      input {
        padding: 10px;
        border-radius: 8px;
        border: 1px solid rgba(255, 255, 255, 0.14);
        background: rgba(0, 0, 0, 0.25);
        color: #e7eaf3;
      }
      .actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 12px;
      }
      button {
        padding: 10px 14px;
        border-radius: 8px;
        border: none;
        background: #5865f2;
        color: #fff;
        cursor: pointer;
      }
      button:disabled {
        opacity: 0.6;
        cursor: default;
      }
      .error {
        margin-top: 12px;
        color: #ffb4b4;
      }
      a {
        color: #cbd2ff;
      }
    `,
  ],
})
export class LoginPage {
  email = '';
  password = '';
  loading = signal(false);
  error = signal<string | null>(null);

  constructor(private auth: AuthService, private router: Router) {}

  submit() {
    this.error.set(null);
    this.loading.set(true);
    this.auth.login(this.email, this.password).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigateByUrl('/dashboard');
      },
      error: (e) => {
        this.loading.set(false);
        this.error.set(e?.status === 401 ? 'Invalid credentials' : 'Login failed');
      },
    });
  }
}

