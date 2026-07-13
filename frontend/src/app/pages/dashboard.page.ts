import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="nav">
      <a routerLink="/dashboard" class="nav-link active">Dashboard</a>
      <a routerLink="/customers" class="nav-link">Customers</a>
      <a routerLink="/user" class="nav-link">My Profile</a>
      <button (click)="logout()" class="logout-btn">Logout</button>
    </nav>

    <div class="grid">
      <div class="card">
        <h2>Session</h2>
        <p class="hint">JWT is stored in localStorage and attached via interceptor.</p>
      </div>

      <div class="card">
        <h2>My projects</h2>
        <button (click)="loadProjects()" [disabled]="loading()">Reload</button>
        <pre>{{ projects() }}</pre>
      </div>
    </div>
  `,
  styles: [
    `
      .nav {
        display: flex;
        gap: 12px;
        padding: 16px 20px;
        background: rgba(255, 255, 255, 0.04);
        border-radius: 12px;
        margin-bottom: 20px;
        align-items: center;
      }
      .nav-link {
        padding: 8px 16px;
        border-radius: 8px;
        text-decoration: none;
        color: #fff;
        background: rgba(255, 255, 255, 0.08);
        transition: background 0.2s;
      }
      .nav-link:hover, .nav-link.active {
        background: #5865f2;
      }
      .logout-btn {
        margin-left: auto;
        padding: 8px 16px;
        border-radius: 8px;
        border: none;
        background: #dc2626;
        color: #fff;
        cursor: pointer;
      }
      .grid {
        display: grid;
        grid-template-columns: 1fr;
        gap: 14px;
      }
      @media (min-width: 900px) {
        .grid {
          grid-template-columns: 1fr 2fr;
        }
      }
      .card {
        padding: 16px;
        border-radius: 12px;
        background: rgba(255, 255, 255, 0.04);
        border: 1px solid rgba(255, 255, 255, 0.08);
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
        cursor: not-allowed;
      }
      pre {
        background: rgba(0, 0, 0, 0.25);
        padding: 10px;
        border-radius: 8px;
        overflow: auto;
        margin-top: 10px;
      }
      .hint {
        opacity: 0.85;
      }
    `,
  ],
})
export class DashboardPage {
  loading = signal(false);
  projects = signal<string>('[]');

  constructor(
    private http: HttpClient,
    private auth: AuthService,
  ) {
    this.loadProjects();
  }

  loadProjects() {
    this.loading.set(true);
    this.http.get('/api/business/projects').subscribe({
      next: (r) => {
        this.loading.set(false);
        this.projects.set(JSON.stringify(r, null, 2));
      },
      error: () => {
        this.loading.set(false);
        this.projects.set('Failed to load projects');
      },
    });
  }

  logout() {
    this.auth.logout();
    location.href = '/login';
  }
}

