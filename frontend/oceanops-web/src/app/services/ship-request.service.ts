import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ShipRequest } from '../models/ship-request.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ShipRequestService {

  private apiUrl = 'http://localhost:8080/ship-requests';

  constructor(private http: HttpClient) { }

  getAll(): Observable<ShipRequest[]> {
    return this.http.get<ShipRequest[]>(`${this.apiUrl}/all`);
  }

  updateStatus(id: number, status: string): Observable<ShipRequest> {
    return this.http.put<ShipRequest>(`${this.apiUrl}/${id}/status/${status}`, {});
  }

  analyze(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/ai`);
  }



  createRequest(data: any) {
    return this.http.post(`${this.apiUrl}/create`, data);
  }
  uploadAttachment(id: number, file: File) {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(
      `${this.apiUrl}/${id}/upload`,
      formData,
      {
        responseType: 'text'
      }
    );
  }
  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

}
