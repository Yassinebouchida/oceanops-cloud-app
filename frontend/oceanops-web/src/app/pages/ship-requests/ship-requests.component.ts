import { Component, OnInit } from '@angular/core';
import { ShipRequestService } from '../../services/ship-request.service';
import { ShipRequest } from '../../models/ship-request.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-ship-requests',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './ship-requests.component.html',
  styleUrls: ['./ship-requests.component.scss']
})
export class ShipRequestsComponent implements OnInit {

  requests: ShipRequest[] = [];
  loading = false;

  // AI State
  selectedRequestId: number | null = null;
  aiLoading = false;
  aiError: string | null = null;
  aiResult: any = null;

  constructor(private shipRequestService: ShipRequestService) { }

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests() {
    this.loading = true;
    this.shipRequestService.getAll().subscribe({
      next: (data) => {
        const userRaw = localStorage.getItem('user');
        if (userRaw) {
          const user = JSON.parse(userRaw);
          if (user.role === 'CLIENT') {
            // Frontend filtering: Show only my requests
            this.requests = data.filter(r => r.clientId === user.id);
          } else {
            // Admin/Agent see all (or verify agent logic later)
            this.requests = data;
          }
        } else {
          this.requests = data;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading requests:', err);
        this.loading = false;
      }
    });
  }

  updateStatus(id: number, status: string) {
    this.shipRequestService.updateStatus(id, status).subscribe({
      next: () => this.loadRequests(),
      error: (err) => console.error(err)
    });
  }

  // --------------------------
  // AI ANALYSIS LOGIC
  // --------------------------
  aiAnalyze(id: number) {
    this.selectedRequestId = id;
    this.aiLoading = true;
    this.aiError = null;
    this.aiResult = null;

    this.shipRequestService.analyze(id).subscribe({
      next: (res) => {
        console.log("AI RAW RESPONSE:", res);

        try {
          // If backend already returns an object → use it
          if (typeof res === "object") {
            this.aiResult = res;
          }
          // If backend returns a JSON string → parse it
          else if (typeof res === "string") {
            this.aiResult = JSON.parse(res);
          }
        } catch (e) {
          console.error("JSON PARSE ERROR:", e);
          this.aiError = "AI did not return JSON response.";
        }

        this.aiLoading = false;
      },
      error: (err) => {
        console.error("AI ERROR:", err);
        this.aiError = "AI analysis failed.";
        this.aiLoading = false;
      }
    });
  }

  closeAiPanel() {
    this.selectedRequestId = null;
    this.aiError = null;
    this.aiResult = null;
  }

  get userRole(): string {
    const userRaw = localStorage.getItem('user');
    return userRaw ? JSON.parse(userRaw).role : '';
  }
}
