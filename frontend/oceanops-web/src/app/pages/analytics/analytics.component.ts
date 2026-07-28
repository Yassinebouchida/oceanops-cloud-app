import { Component, OnInit, OnDestroy } from '@angular/core';
import { AnalyticsService } from '../../services/analytics.service';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit, OnDestroy {

  statusChart?: Chart<'pie', number[], string>;
  portChart?: Chart<'bar', number[], string>;
  summary: any = {};

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadRequestsByStatusChart();
    this.loadRequestsByPortChart();
    this.loadSummary();
  }

  ngOnDestroy(): void {
    this.statusChart?.destroy();
    this.portChart?.destroy();
  }

  // ===============================
  // Requests by Status (Pie)
  // ===============================
  loadRequestsByStatusChart(): void {
    this.analyticsService.getRequestsByStatus().subscribe(data => {

      const labels = Object.keys(data);
      const values = Object.values(data) as number[];

      this.statusChart?.destroy();

      this.statusChart = new Chart(
        'requestsStatusChart',
        {
          type: 'pie',
          data: {
            labels,
            datasets: [
              {
                data: values,
                backgroundColor: [
                  '#3b82f6',
                  '#ef4444',
                  '#f59e0b',
                  '#10b981'
                ]
              }
            ]
          },
          options: {
            responsive: true,
            plugins: {
              legend: { position: 'bottom' }
            }
          }
        } 
      );
    });
  }

  // ===============================
  // Requests by Port (Bar)
  // ===============================
  loadRequestsByPortChart(): void {
    this.analyticsService.getRequestsByPort().subscribe(data => {

      const labels = Object.keys(data);
      const values = Object.values(data) as number[];

      this.portChart?.destroy();

      this.portChart = new Chart(
        'requestsPortChart',
        {
          type: 'bar',
          data: {
            labels,
            datasets: [
              {
                label: 'Requests',
                data: values,
                backgroundColor: '#3b82f6'
              }
            ]
          },
          options: {
            responsive: true,
            plugins: {
              legend: { display: false }
            },
            scales: {
              y: { beginAtZero: true }
            }
          }
        } 
      );
    });
  }

  // ===============================
  // Dashboard Summary
  // ===============================
  loadSummary(): void {
    this.analyticsService.getDashboardSummary()
      .subscribe(res => this.summary = res);
  }
}
