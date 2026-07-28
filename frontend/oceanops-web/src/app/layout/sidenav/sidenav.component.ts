import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [
    CommonModule,
    MatListModule,
    MatIconModule,
    RouterModule
  ],
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})
export class SidenavComponent {

  currentUser: any = null;

  ngOnInit() {
    const userRaw = localStorage.getItem('user');
    if (userRaw) {
      this.currentUser = JSON.parse(userRaw);
    }
  }

  get isClient() {
    return this.currentUser?.role === 'CLIENT';
  }

  get isAdmin() {
    return this.currentUser?.role === 'ADMIN';
  }
}
