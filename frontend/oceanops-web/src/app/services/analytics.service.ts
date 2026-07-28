import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  private apiUrl = 'http://localhost:8080/analytics';

  constructor(private http: HttpClient) {}

  getRequestsByStatus(): Observable<any> {
    return this.http.get(`${this.apiUrl}/requests-by-status`);
  }

  getRequestsByPort(): Observable<any> {
    return this.http.get(`${this.apiUrl}/requests-by-port`);
  }

  getDashboardSummary(): Observable<any> {
    return this.http.get(`${this.apiUrl}/dashboard-summary`);
  }
}
