import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ShipRequestService } from '../../services/ship-request.service';
import { CommonModule, NgClass } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-ship-request-details',
  standalone: true,
  imports: [CommonModule, RouterModule, NgClass],
  templateUrl: './ship-request-details.component.html',
  styleUrls: ['./ship-request-details.component.scss']
})
export class ShipRequestDetailsComponent implements OnInit {

  request: any = null;       // ✅ THIS fixes the HTML error
  ai: any = null;
  loading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private service: ShipRequestService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.service.getById(id).subscribe({
      next: res => this.request = res,
      error: err => console.error(err)
    });
  }

  runAi() {
    this.loading = true;
    this.error = '';
    this.ai = null;

    this.service.analyze(this.request.id).subscribe({
      next: res => {
        this.ai = res;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.error = "AI failed to analyze this request.";
        this.loading = false;
      }
    });
  }
  updateStatus(status: string) {
    if (!confirm(`Are you sure you want to ${status} this request?`)) {
      return;
    }

    this.service.updateStatus(this.request.id, status).subscribe({
      next: () => {
        alert(`Request ${status} successfully`);
        this.request.status = status;
      },
      error: err => {
        console.error(err);
        alert("Failed to update status");
      }

    });
  }
  download(file: any) {
    window.open(
      `http://localhost:8080/ship-requests/download/${file.id}`,
      '_blank'
    );
  }
  get userRole(): string {
    const userRaw = localStorage.getItem('user');
    return userRaw ? JSON.parse(userRaw).role : '';
  }
}


