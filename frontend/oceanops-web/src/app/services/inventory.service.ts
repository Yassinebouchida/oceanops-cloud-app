import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InventoryItem {
    id: number;
    name: string;
    category: string;
    quantity: number;
    unit: string;
    price?: number;
}

@Injectable({
    providedIn: 'root'
})
export class InventoryService {

    private apiUrl = 'http://localhost:8080/inventory';

    constructor(private http: HttpClient) { }

    getAll(): Observable<InventoryItem[]> {
        // Add timestamp to prevent caching
        return this.http.get<InventoryItem[]>(`${this.apiUrl}?_t=${new Date().getTime()}`);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
