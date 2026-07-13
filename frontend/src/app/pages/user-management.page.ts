import { Component, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

interface User {
  id: string;
  email: string;
  displayName: string;
  roles: string[];
}

@Component({
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <h2>User Management</h2>
      <button (click)="loadUsers()" [disabled]="loading()">Reload</button>
      <table *ngIf="users().length; else empty">
        <thead>
          <tr><th>Email</th><th>Name</th><th>Roles</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let user of users()">
            <td>{{ user.email }}</td>
            <td>{{ user.displayName }}</td>
            <td>{{ user.roles.join(', ') }}</td>
          </tr>
        </tbody>
      </table>
      <ng-template #empty><p>No users found.</p></ng-template>
    </div>
  `,
  styles: [
    `
      .card {
        padding: 16px;
        border-radius: 12px;
        background: rgba(255, 255, 255, 0.04);
        border: 1px solid rgba(255, 255, 255, 0.08);
        margin-top: 24px;
      }
      table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 16px;
      }
      th, td {
        padding: 8px 12px;
        border-bottom: 1px solid #333;
      }
      th {
        text-align: left;
      }
      button {
        padding: 10px 14px;
        border-radius: 8px;
        border: none;
        background: #5865f2;
        color: #fff;
        cursor: pointer;
        margin-bottom: 12px;
      }
    `
  ]
})
export class UserManagementPage {
  users = signal<User[]>([]);
  loading = signal(false);

  constructor(private http: HttpClient, private auth: AuthService) {
    this.loadUsers();
  }

  loadUsers() {
    this.loading.set(true);
    this.http.get<User[]>('/api/users').subscribe({
      next: (data) => this.users.set(data),
      error: () => this.users.set([]),
      complete: () => this.loading.set(false)
    });
  }
}
