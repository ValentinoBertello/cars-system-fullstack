import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink, RouterModule, RouterOutlet } from '@angular/router';
import Swal from 'sweetalert2';
import { AuthService } from '../../services/users/auth.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [RouterModule, RouterLink, CommonModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {

  currentYear = new Date().getFullYear();

  // stats simples para portfolio (estáticos). Reemplazá con datos reales si querés.
  stats = {
    cars: 42,
    clients: 128,
    sales: 18
  };

  constructor(public authService: AuthService, private router: Router) { }

  requireAuthNavigate(route: string): void {
    // si está autenticado, navegamos directamente
    if (this.authService.isAuthenticated()) {
      this.router.navigate([route]);
      return;
    }

    // si NO está autenticado, mostramos el Swal
    Swal.fire({
      title: 'Necesitas iniciar sesión',
      text: 'Para acceder a esta función debes iniciar sesión o registrarte.',
      icon: 'warning',
      showDenyButton: true,
      showCancelButton: true,
      confirmButtonText: 'Iniciar sesión',
      denyButtonText: 'Registrarse',
      cancelButtonText: 'Cancelar',
      // Estética acorde a tu paleta
      background: '#0b0b0b',
      color: '#ffffff',
      confirmButtonColor: '#04dbf7',
      denyButtonColor: '#00e5e5'
    }).then((result) => {
      if (result.isConfirmed) {
        this.router.navigate(['/user/login']);
      } else if (result.isDenied) {
        this.router.navigate(['/user/create']);
      }
      // si canceló, no hacemos nada
    });
  }
}
