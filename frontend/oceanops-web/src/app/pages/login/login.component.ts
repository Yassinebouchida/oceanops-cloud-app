import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  email = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService) { }

  login() {
    if (!this.email || !this.password) {
      this.errorMessage = "Please enter both email and password.";
      return;
    }

    this.authService.login(this.email, this.password).subscribe({
      next: (response: any) => {
        // 🔥 SAVE USER AND TOKEN
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));

        window.location.href = "/ship-requests";
      },
      error: () => {
        this.errorMessage = "Invalid email or password.";
      }
    });
  }
}
