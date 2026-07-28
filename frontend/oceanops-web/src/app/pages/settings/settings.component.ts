import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit {

  activeTab: string = 'profile'; // 'profile', 'security', 'preferences'

  currentUser: any = {
    fullName: '',
    email: '',
    role: '',
    phone: '+212 600 000 000'
  };

  // Password Form
  passwordForm = {
    current: '',
    new: '',
    confirm: ''
  };

  // Preferences
  preferences = {
    darkMode: false,
    emailNotifs: true,
    systemAlerts: false
  };

  isLoading = false;

  ngOnInit(): void {
    const userRaw = localStorage.getItem('user');
    if (userRaw) {
      this.currentUser = JSON.parse(userRaw);
      // Ensure phone exists
      if (!this.currentUser.phone) this.currentUser.phone = '+212 600 000 000';
    }
  }

  saveProfile() {
    this.isLoading = true;
    setTimeout(() => {
      this.isLoading = false;
      // Simulate save
      localStorage.setItem('user', JSON.stringify(this.currentUser));
      alert('Profile updated successfully!');
    }, 1000);
  }

  updatePassword() {
    if (this.passwordForm.new !== this.passwordForm.confirm) {
      alert('Passwords do not match!');
      return;
    }
    this.isLoading = true;
    setTimeout(() => {
      this.isLoading = false;
      alert('Password changed successfully!');
      this.passwordForm = { current: '', new: '', confirm: '' };
    }, 1000);
  }

  toggleDarkMode() {
    // Mock implementation
    console.log('Dark mode:', this.preferences.darkMode);
  }

  logout() {
    if (confirm('Are you sure you want to logout?')) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
  }
}
