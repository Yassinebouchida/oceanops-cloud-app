import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../services/order.service';
import { OrderDTO } from '../../models/order.dto';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss'],
})
export class OrdersComponent implements OnInit {

  orders: OrderDTO[] = [];
  loading = false;

  constructor(private orderService: OrderService) { }

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.orderService.getAllOrders().subscribe({
      next: (data) => {
        const userRaw = localStorage.getItem('user');
        if (userRaw) {
          const user = JSON.parse(userRaw);
          if (user.role === 'CLIENT') {
            // Frontend filtering: Show only my orders
            this.orders = data.filter(o => o.clientId === user.id);
          } else {
            this.orders = data;
          }
        } else {
          this.orders = data;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading orders:', err);
        this.loading = false;
      }
    });
  }

  deleteOrder(id: number) {
    if (!confirm("Are you sure you want to delete this order?")) return;

    this.orderService.deleteOrder(id).subscribe({
      next: () => this.loadOrders(),
      error: (err) => console.error(err)
    });
  }

  updateStatus(orderId: number, newStatus: string) {
    this.orderService.updateStatus(orderId, newStatus).subscribe({
      next: () => this.loadOrders(),
      error: (err) => console.error(err)
    });
  }

  reportAnomaly(orderId: number) {
    const comment = prompt("Please describe the anomaly:");
    if (!comment) return;

    this.orderService.reportAnomaly(orderId, comment).subscribe({
      next: () => {
        alert("Anomaly reported successfully.");
        this.loadOrders();
      },
      error: (err) => console.error(err)
    });
  }

  selectedAnomaly: string | null = null;
  selectedComments: any[] = [];

  showAnomalyDetails(orderId: number, comment: string) {
    this.selectedOrderId = orderId;
    this.selectedAnomaly = comment;

    this.orderService.getComments(orderId).subscribe({
      next: (comments: any[]) => {
        this.selectedComments = comments;
      },
      error: (err: any) => console.error(err)
    });
  }

  closeAnomalyDetails() {
    this.selectedAnomaly = null;
  }
  replyText: string = '';
  selectedOrderId: number | null = null;

  sendReply() {
    if (!this.replyText.trim() || !this.selectedOrderId) {
      return;
    }

    const userRaw = localStorage.getItem('user');
    const user = userRaw ? JSON.parse(userRaw) : null;

    if (!user) return;

    this.orderService.addComment(
      this.selectedOrderId,
      user.id,
      this.replyText
    ).subscribe({
      next: () => {
        alert('Reply sent successfully.');
        this.replyText = '';
        if (this.selectedOrderId !== null) {
          this.showAnomalyDetails(this.selectedOrderId, this.selectedAnomaly || '');
        } this.loadOrders();

      },
      error: (err: any) => console.error(err)
    });
  }



  getStatusColor(status: string) {
    switch (status) {
      case 'CREATED': return 'blue';
      case 'VALIDATED': return 'purple';
      case 'IN_PREPARATION': return 'orange';
      case 'IN_DELIVERY': return 'teal';
      case 'DELIVERED': return 'green';
      case 'SUPERVISED': return 'gray';
      case 'ANOMALY_REPORTED': return 'red';
      case 'ANOMALY_RESOLVED': return 'green';
      default: return 'black';
    }
  }

  get userRole(): string {
    const userRaw = localStorage.getItem('user');
    return userRaw ? JSON.parse(userRaw).role : '';
  }
}
