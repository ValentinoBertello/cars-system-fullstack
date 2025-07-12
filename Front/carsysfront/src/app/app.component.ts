import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './services/users/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, RouterLink, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {

  constructor(public authService: AuthService, private router: Router) {
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

}
