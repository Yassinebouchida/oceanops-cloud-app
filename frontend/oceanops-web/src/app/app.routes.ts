import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { OrdersComponent } from './pages/orders/orders.component';
import { AnalyticsComponent } from './pages/analytics/analytics.component';
import { NotificationsComponent } from './pages/notifications/notifications.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { LoginComponent } from './pages/login/login.component';
import { ShipRequestsComponent } from './pages/ship-requests/ship-requests.component';
import { InventoryComponent } from './pages/inventory/inventory.component';
import { ShipRequestDetailsComponent } from './pages/ship-requests/ship-request-details.component';
import { authGuard } from './auth.guard';   // ✅ ADD THIS
import { OrderDetailsComponent } from './pages/orders/order-details.component';

export const routes: Routes = [
  // LOGIN (public)
  { path: 'login', component: LoginComponent },

  // ALL OTHER ROUTES ARE PROTECTED
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],      // 🔥 Protect Layout + all children
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'orders/:id', component: OrderDetailsComponent },

      {
        path: 'ship-requests',
        children: [

          {
            path: '',
            component: ShipRequestsComponent
          },

          {
            path: 'create',
            loadComponent: () =>
              import('./pages/ship-requests/create-ship-request.component')
                .then(m => m.CreateShipRequestComponent)
          },

          {
            path: ':id',
            component: ShipRequestDetailsComponent
          }

        ]
      },

      { path: 'inventory', component: InventoryComponent },
      { path: 'analytics', component: AnalyticsComponent },
      { path: 'notifications', component: NotificationsComponent },
      { path: 'settings', component: SettingsComponent },

      // Default route
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  // Fallback
  { path: '**', redirectTo: 'dashboard' }
];
