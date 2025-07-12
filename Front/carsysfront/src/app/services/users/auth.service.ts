import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, tap } from 'rxjs';
import { LoginRequestDto, LoginResponseDto } from '../../models/users';
import { GlobalService } from '../global.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);
  private readonly globalUrlsService = inject(GlobalService);
  private readonly router = inject(Router);
  private apiUrl = this.globalUrlsService.apiUrlLocalHost8085;
  private userEmail = "";

  //Variable reactiva que empieza en false, o sea, diciendo que el usuario no esta autenticado
  //se puede escuchar o suscribirse a su valor y se actualiza automaticamente
  private authStatus = new BehaviorSubject<boolean>(false);

  //Convertimos el BehaviorSubject en un observable solo de lectura, para que otros componentes
  //puedan escuchar si el estado cambia, pero no puedan modificarlo
  authStatus$ = this.authStatus.asObservable();

  login(loginDto: LoginRequestDto) {
    return this.http.post<LoginResponseDto>(`${this.apiUrl}/login`, loginDto).pipe(
      tap(response => {

        this.userEmail = response.username;

        //El localStorage es una forma de guardar datos en el navegador del usuario,
        //  como si fuera una pequeña "base de datos" que se guarda en su computadora.

        // Guardar el token en localStorage
        localStorage.setItem('token', response.token);

        // Actualiza el estado a "autenticado"
        this.authStatus.next(true);
      }),
      catchError(error => {
        if (error.status === 401) {
          throw new Error('Credenciales incorrectas');
        }
        throw new Error('Error en el servidor');
      })
    );
  }

  // Retorna el token almacenado en el localStorage
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Cerramos sesión, o sea, se borra el token, el estado de authStatus es falso (no autenticado)
  // y volvemos al login
  logout(): void {
    localStorage.removeItem('token');
    this.authStatus.next(false);
    this.router.navigate(['/login']);
  }

  // Nos dice si el usuario esta o no autenticado 
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // Método para obtener email
  getCurrentUserEmail(): string {
    const token = this.getToken();
    if (!token) return '';

    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.username; // O el campo donde guardas el email en tu JWT
  }

  // token.split('.'): Divide el token en sus 3 partes
  // atob(): Decodifica de Base64 a texto plano
  // JSON.parse(): Convierte el texto JSON en un objeto JavaScript
  getCurrentUserRoles(): string[] {
    const token = this.getToken();
    if (!token) return [];
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.authorities || [];
  }

  hasRole(role: string): boolean {
    const roles = this.getCurrentUserRoles();
    return roles.includes(role);
  }
}
