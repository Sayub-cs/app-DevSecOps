import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CustomerService, Customer } from '../customer/customer.service';
import { AuthService } from '../auth/auth.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <nav class="nav">
      <a routerLink="/dashboard" class="nav-link">Dashboard</a>
      <a routerLink="/customers" class="nav-link active">Customers</a>
      <a routerLink="/user" class="nav-link">My Profile</a>
      <button (click)="logout()" class="logout-btn">Logout</button>
    </nav>

    <div class="content">
      <div class="card">
        <div class="header">
          <h2>Customers ({{ customers().length }})</h2>
          <button (click)="showForm.set(true)" *ngIf="!showForm()">+ Add Customer</button>
        </div>

        <div *ngIf="showForm()" class="form">
          <input [(ngModel)]="formName" placeholder="Name *" />
          <input [(ngModel)]="formEmail" placeholder="Email" />
          <input [(ngModel)]="formPhone" placeholder="Phone" />
          <input [(ngModel)]="formAddress" placeholder="Address" />
          <div class="form-actions">
            <button (click)="saveCustomer()" [disabled]="saving()">{{ editingId ? 'Update' : 'Create' }}</button>
            <button (click)="cancelForm()" class="secondary">Cancel</button>
          </div>
        </div>

        <table *ngIf="customers().length > 0">
          <thead>
            <tr><th>Name</th><th>Email</th><th>Phone</th><th>Address</th><th>Actions</th></tr>
          </thead>
          <tbody>
            <tr *ngFor="let c of customers()">
              <td>{{ c.name }}</td>
              <td>{{ c.email || '-' }}</td>
              <td>{{ c.phone || '-' }}</td>
              <td>{{ c.address || '-' }}</td>
              <td>
                <button (click)="editCustomer(c)" class="small">Edit</button>
                <button (click)="deleteCustomer(c.id)" class="small danger">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>

        <p *ngIf="customers().length === 0 && !showForm()" class="empty">No customers yet. Add one!</p>
      </div>
    </div>
  `,
  styles: [`
    .nav { display: flex; gap: 12px; padding: 16px 20px; background: rgba(255,255,255,0.04); border-radius: 12px; margin-bottom: 20px; }
    .nav-link { padding: 8px 16px; border-radius: 8px; text-decoration: none; color: #fff; background: rgba(255,255,255,0.08); }
    .nav-link:hover, .nav-link.active { background: #5865f2; }
    .logout-btn { margin-left: auto; padding: 8px 16px; border-radius: 8px; border: none; background: #dc2626; color: #fff; cursor: pointer; }
    .content { padding: 20px; }
    .card { padding: 20px; border-radius: 12px; background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.08); }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
    .header h2 { margin: 0; }
    button { padding: 10px 14px; border-radius: 8px; border: none; background: #5865f2; color: #fff; cursor: pointer; }
    button:disabled { opacity: 0.6; }
    button.small { padding: 6px 10px; font-size: 12px; }
    button.secondary { background: rgba(255,255,255,0.15); }
    button.danger { background: #dc2626; }
    .form { display: flex; flex-direction: column; gap: 10px; margin-bottom: 16px; padding: 16px; background: rgba(0,0,0,0.2); border-radius: 8px; }
    .form input { padding: 10px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.2); background: rgba(0,0,0,0.3); color: #fff; }
    .form-actions { display: flex; gap: 10px; }
    table { width: 100%; border-collapse: collapse; }
    th, td { text-align: left; padding: 12px; border-bottom: 1px solid rgba(255,255,255,0.1); }
    th { opacity: 0.7; }
    .empty { opacity: 0.7; text-align: center; padding: 40px; }
  `]
})
export class CustomersPage {
  customers = signal<Customer[]>([]);
  showForm = signal(false);
  saving = signal(false);
  editingId: string | null = null;

  formName = '';
  formEmail = '';
  formPhone = '';
  formAddress = '';

  constructor(private customerService: CustomerService, private auth: AuthService) {
    this.loadCustomers();
  }

  loadCustomers() {
    this.customerService.getCustomers().subscribe({
      next: (r) => this.customers.set(r),
      error: () => this.customers.set([])
    });
  }

  editCustomer(c: Customer) {
    this.editingId = c.id;
    this.formName = c.name;
    this.formEmail = c.email;
    this.formPhone = c.phone;
    this.formAddress = c.address;
    this.showForm.set(true);
  }

  saveCustomer() {
    if (!this.formName.trim()) return;
    this.saving.set(true);
    const data = {
      name: this.formName,
      email: this.formEmail || undefined,
      phone: this.formPhone || undefined,
      address: this.formAddress || undefined
    };
    const obs = this.editingId
      ? this.customerService.updateCustomer(this.editingId, data)
      : this.customerService.createCustomer(data);
    obs.subscribe({
      next: () => {
        this.saving.set(false);
        this.cancelForm();
        this.loadCustomers();
      },
      error: () => this.saving.set(false)
    });
  }

  deleteCustomer(id: string) {
    if (!confirm('Delete this customer?')) return;
    this.customerService.deleteCustomer(id).subscribe({
      next: () => this.loadCustomers()
    });
  }

  cancelForm() {
    this.showForm.set(false);
    this.editingId = null;
    this.formName = '';
    this.formEmail = '';
    this.formPhone = '';
    this.formAddress = '';
  }

  logout() {
    this.auth.logout();
    location.href = '/login';
  }
}