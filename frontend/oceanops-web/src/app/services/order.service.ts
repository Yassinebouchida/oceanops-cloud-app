import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderDTO } from '../models/order.dto';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private baseUrl = 'http://localhost:8080/orders';

  constructor(private http: HttpClient) { }

  getAllOrders(): Observable<OrderDTO[]> {
    return this.http.get<OrderDTO[]>(`${this.baseUrl}/all`);
  }

  updateStatus(orderId: number, status: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${orderId}/status/${status}`, {});
  }

  reportAnomaly(id: number, comment: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${id}/anomaly`, comment);
  }

  deleteOrder(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
  addComment(orderId: number, userId: number, comment: string) {
    return this.http.post(
      `${this.baseUrl}/${orderId}/comments?userId=${userId}&comment=${encodeURIComponent(comment)}`,
      {}
    );
  }
  getComments(orderId: number) {
    return this.http.get<any[]>(
      `${this.baseUrl}/${orderId}/comments`
    );
  }
  getById(id: number) {
    return this.http.get<any>(`http://localhost:8080/orders/${id}`);
  }

  getHistory(id: number) {
    return this.http.get<any[]>(`http://localhost:8080/orders/${id}/history`);
  }
}
