import { Routes } from '@angular/router';

import { AuthGuard } from './auth/auth.guard';
import { LoginPage } from './pages/login.page';
import { RegisterPage } from './pages/register.page';
import { DashboardPage } from './pages/dashboard.page';
import { CustomersPage } from './pages/customers.page';
import { UserPage } from './pages/user.page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'dashboard', component: DashboardPage, canActivate: [AuthGuard] },
  { path: 'customers', component: CustomersPage, canActivate: [AuthGuard] },
  { path: 'user', component: UserPage, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'dashboard' },
];
