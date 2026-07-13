import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, User } from '../user/user.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <h2>User Profile</h2>
      
      <div class="card" *ngIf="user(); else loading">
        <div class="form-group">
          <label>Email</label>
          <input type="email" [(ngModel)]="email" [disabled]="saving()" />
        </div>
        
        <div class="form-group">
          <label>Display Name</label>
          <input type="text" [(ngModel)]="displayName" [disabled]="saving()" />
        </div>
        
        <div class="actions">
          <button (click)="save()" [disabled]="saving()">
            {{ saving() ? 'Saving...' : 'Save Changes' }}
          </button>
          <span class="message" *ngIf="message()">{{ message() }}</span>
        </div>
      </div>
      
      <ng-template #loading>
        <p>Loading...</p>
      </ng-template>
    </div>
  `,
  styles: [`
    .page { padding: 20px; max-width: 600px; margin: 0 auto; }
    .card { 
      padding: 20px; 
      border-radius: 12px; 
      background: rgba(255, 255, 255, 0.04);
      border: 1px solid rgba(255, 255, 255, 0.08);
    }
    .form-group { margin-bottom: 16px; }
    label { display: block; margin-bottom: 6px; font-weight: 500; }
    input { 
      width: 100%; 
      padding: 10px; 
      border-radius: 8px; 
      border: 1px solid rgba(255,255,255,0.2);
      background: rgba(0,0,0,0.3);
      color: #fff;
      font-size: 14px;
    }
    input:disabled { opacity: 0.6; }
    .actions { display: flex; align-items: center; gap: 12px; margin-top: 20px; }
    button {
      padding: 10px 20px;
      border-radius: 8px;
      border: none;
      background: #5865f2;
      color: #fff;
      cursor: pointer;
    }
    button:disabled { opacity: 0.6; cursor: not-allowed; }
    .message { color: #4ade80; font-size: 14px; }
  `]
})
export class UserPage implements OnInit {
  user = signal<User | null>(null);
  email = '';
  displayName = '';
  saving = signal(false);
  message = signal('');

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.userService.getCurrentUser().subscribe({
      next: (u: User) => {
        this.user.set(u);
        this.email = u.email;
        this.displayName = u.displayName;
      },
      error: () => {
        this.message.set('Failed to load user');
      }
    });
  }

  save() {
    this.saving.set(true);
    this.message.set('');
    this.userService.updateCurrentUser({ email: this.email, displayName: this.displayName }).subscribe({
      next: (u: User) => {
        this.user.set(u);
        this.saving.set(false);
        this.message.set('Saved successfully!');
        setTimeout(() => this.message.set(''), 3000);
      },
      error: () => {
        this.saving.set(false);
        this.message.set('Failed to save');
      }
    });
  }
}