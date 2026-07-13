import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Customer {
  id: string;
  name: string;
  email: string;
  phone: string;
  address: string;
}

@Injectable({ providedIn: 'root' })
export class CustomerService {
  constructor(private http: HttpClient) {}

  getCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>('/api/business/customers');
  }

  getCustomer(id: string): Observable<Customer> {
    return this.http.get<Customer>(`/api/business/customers/${id}`);
  }

  createCustomer(data: { name: string; email?: string; phone?: string; address?: string }): Observable<Customer> {
    return this.http.post<Customer>('/api/business/customers', data);
  }

  updateCustomer(id: string, data: { name: string; email?: string; phone?: string; address?: string }): Observable<Customer> {
    return this.http.put<Customer>(`/api/business/customers/${id}`, data);
  }

  deleteCustomer(id: string): Observable<void> {
    return this.http.delete<void>(`/api/business/customers/${id}`);
  }
}