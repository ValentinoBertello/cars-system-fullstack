import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/users/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    //Módulo con directivas básicas como ngIf, ngFor
    CommonModule,

    //Permite el uso de formularios template-driven (ngModel)
    FormsModule,

    //Formularios reactivos (FormGroup, FormControl)
    ReactiveFormsModule,

    //Usar routerlink para navegar
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  // 1. Propiedades públicas
  public isLoading = false;
  public mostrarPassword = false;

  //2. Formulario Reactivo
  public formReactivo: FormGroup;

  //3. Inyección de dependencias
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  //4. Constructor
  constructor() {
    this.formReactivo = new FormGroup({
      email: new FormControl('demo@gmail.com', [
        Validators.required,
        Validators.email
      ]),
      password: new FormControl('123456', [
        Validators.required,
        Validators.minLength(6)
      ])
    });
  }

  //5. Métodos públicos (interacción con la vista)
  public togglePasswordVisibility(): void {
    this.mostrarPassword = !this.mostrarPassword;
  }

  //6. Método principal de Submit
  public onSubmitForm(): void {

    this.isLoading = true;
    const { email, password } = this.formReactivo.value;

    this.authService.login({ email, password }).subscribe({
      next: () => this.handleLoginSuccess(),
      error: () => this.handleLoginError(),
      complete: () => this.isLoading = false
    });
  }

  //7. Métodos privados (lógica interna)

  // Desplegamos swal de éxito
  private handleLoginSuccess(): void {
    Swal.fire({
      title: '¡Inicio de sesión exitoso!',
      icon: 'success',
      timer: 1300,
      timerProgressBar: true,
      showConfirmButton: false
    }).then(() => {
      this.router.navigate(['/landing']);
    });
  }

  // Desplegamos swal de credenciales incorrectas
  private handleLoginError(): void {
    this.isLoading = false;
    Swal.fire({
      title: 'Error',
      text: 'Credenciales incorrectas',
      icon: 'error',
      confirmButtonText: 'Cerrar'
    });
  }

}