import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../services/dashboard.service';
import { ShipRequestService } from '../../services/ship-request.service';
import { OrderService } from '../../services/order.service';
import { InventoryService } from '../../services/inventory.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  totalOrders = 0;
  pendingRequests = 0;
  inventoryItems = 0;

  // Role helper
  isAdmin = false;

  // Stats for charts
  orderStats: { label: string, count: number, pct: number, color: string }[] = [];
  inventoryStats: { label: string, count: number, pct: number, color: string }[] = [];

  constructor(
    private dashboardService: DashboardService,
    private shipRequestService: ShipRequestService,
    private orderService: OrderService,
    private inventoryService: InventoryService
  ) { }

  ngOnInit(): void {
    const userRaw = localStorage.getItem('user');

    if (userRaw) {
      const user = JSON.parse(userRaw);

      if (user.role === 'CLIENT') {
        // CLIENT LOGIC: Calculate stats manually from filtered lists
        this.loadClientStats(user.id);
      } else if (user.role === 'AGENT') {
        // AGENT LOGIC: Restricted view
        this.loadAgentStats();
      } else {
        // ADMIN LOGIC: Use global summary
        this.isAdmin = true;
        this.loadAdminStats();
      }
    } else {
      this.loadAdminStats();
    }
  }

  loadAgentStats() {
    // Agents need to see Pending Requests and active Orders to work on them.

    // 1. Pending Requests
    this.shipRequestService.getAll().subscribe(requests => {
      // Agents care about APPROVED or requests they need to process
      this.pendingRequests = requests.filter(r => r.status === 'CREATED' || r.status === 'APPROVED').length;
    });

    // 2. Total Orders
    this.orderService.getAllOrders().subscribe(orders => {
      this.totalOrders = orders.length;

      // Also populate the Order Chart for Agents so they see status breakdown
      const total = orders.length;
      if (total === 0) return;

      const counts: any = {};
      orders.forEach(o => {
        counts[o.status] = (counts[o.status] || 0) + 1;
      });

      this.orderStats = [
        { label: 'Created', key: 'CREATED', color: '#3b82f6' },
        { label: 'Validated', key: 'VALIDATED', color: '#8b5cf6' },
        { label: 'In Prep', key: 'IN_PREPARATION', color: '#f59e0b' },
        { label: 'In Delivery', key: 'IN_DELIVERY', color: '#06b6d4' },
        { label: 'Delivered', key: 'DELIVERED', color: '#10b981' },
        { label: 'Supervised', key: 'SUPERVISED', color: '#6b7280' },
        { label: 'Anomaly', key: 'ANOMALY_REPORTED', color: '#ef4444' }
      ].map(item => {
        const count = counts[item.key] || 0;
        return {
          label: item.label,
          count: count,
          pct: (count / total) * 100,
          color: item.color
        };
      }).filter(i => i.count > 0);
    });

    // 3. Inventory (Optional: Keep 0 or show count?)
    // User restriction says Agents shouldn't see Inventory Page. 
    // I will leave it as 0 to be consistent with the restricted access.
    this.inventoryItems = 0;
  }

  loadClientStats(clientId: number) {
    this.shipRequestService.getAll().subscribe(requests => {
      const myRequests = requests.filter(r => r.clientId === clientId);
      this.pendingRequests = myRequests.filter(r => r.status === 'CREATED' || r.status === 'APPROVED').length;
    });

    this.orderService.getAllOrders().subscribe(orders => {
      const myOrders = orders.filter(o => o.clientId === clientId);
      this.totalOrders = myOrders.length;

      // Populate Chart for Client
      const total = myOrders.length;
      if (total === 0) return;

      const counts: any = {};
      myOrders.forEach(o => {
        counts[o.status] = (counts[o.status] || 0) + 1;
      });

      this.orderStats = [
        { label: 'Created', key: 'CREATED', color: '#3b82f6' },
        { label: 'Validated', key: 'VALIDATED', color: '#8b5cf6' },
        { label: 'In Prep', key: 'IN_PREPARATION', color: '#f59e0b' },
        { label: 'In Delivery', key: 'IN_DELIVERY', color: '#06b6d4' },
        { label: 'Delivered', key: 'DELIVERED', color: '#10b981' },
        { label: 'Supervised', key: 'SUPERVISED', color: '#6b7280' },
        { label: 'Anomaly', key: 'ANOMALY_REPORTED', color: '#ef4444' }
      ].map(item => {
        const count = counts[item.key] || 0;
        return {
          label: item.label,
          count: count,
          pct: (count / total) * 100,
          color: item.color
        };
      }).filter(i => i.count > 0);
    });

    this.inventoryItems = 0;
  }

  loadAdminStats() {
    // 1. Get KPI Summary
    this.dashboardService.getSummary().subscribe({
      next: data => {
        this.totalOrders = data.totalOrders;
        this.pendingRequests = data.pendingRequests;
      },
      error: err => console.error(err)
    });

    // 2. Fetch Inventory Real Data
    this.inventoryService.getAll().subscribe(items => {
      this.inventoryItems = items.length;

      const categoryCounts: any = {};
      items.forEach(i => {
        categoryCounts[i.category] = (categoryCounts[i.category] || 0) + i.quantity;
      });

      const totalQuantity = items.reduce((sum, i) => sum + i.quantity, 0);

      // Colors for categories
      const colors: any = {
        'Provisions': '#10b981', // green
        'Fuel': '#f59e0b',       // orange
        'Spare Parts': '#3b82f6', // blue
        'Safety': '#ef4444',     // red
        'Other': '#6b7280'
      };

      this.inventoryStats = Object.keys(categoryCounts).map(cat => {
        return {
          label: cat,
          count: categoryCounts[cat], // Sum of quantities
          pct: (categoryCounts[cat] / totalQuantity) * 100,
          color: colors[cat] || colors['Other']
        };
      });
    });

    // 3. Get Orders for Chart
    this.orderService.getAllOrders().subscribe(orders => {
      // ... existing order chart logic ...
      const total = orders.length;
      if (total === 0) return;

      const counts: any = {};
      orders.forEach(o => {
        counts[o.status] = (counts[o.status] || 0) + 1;
      });

      // Map to array for UI
      this.orderStats = [
        { label: 'Created', key: 'CREATED', color: '#3b82f6' },
        { label: 'Validated', key: 'VALIDATED', color: '#8b5cf6' },
        { label: 'In Prep', key: 'IN_PREPARATION', color: '#f59e0b' },
        { label: 'In Delivery', key: 'IN_DELIVERY', color: '#06b6d4' },
        { label: 'Delivered', key: 'DELIVERED', color: '#10b981' },
        { label: 'Supervised', key: 'SUPERVISED', color: '#6b7280' },
        { label: 'Anomaly', key: 'ANOMALY_REPORTED', color: '#ef4444' }
      ].map(item => {
        const count = counts[item.key] || 0;
        return {
          label: item.label,
          count: count,
          pct: (count / total) * 100,
          color: item.color
        };
      }).filter(i => i.count > 0);
    });
  }
}