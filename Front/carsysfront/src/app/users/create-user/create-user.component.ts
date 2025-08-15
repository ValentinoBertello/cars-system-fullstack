import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../../services/users/user.service';
import { catchError, map, Observable, of } from 'rxjs';
import { UserPost } from '../../models/users';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-create-user',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './create-user.component.html',
  styleUrl: './create-user.component.css'
})
export class CreateUserComponent {

  //1. PROPIEDADES PÚBLICAS
  public isLoading = false;
  mostrarPassword: boolean = false;
  mostrarPasswordRepeat: boolean = false;

  //2. FORMULARIO REACTIVO
  formReactivo: FormGroup;

  //3. INYECCIÓN DE DEPENDENCIAS
  private readonly userService = inject(UserService);

  //4. CONSTRUCTOR
  constructor(private router: Router) {

    // Inicializamos el formulario
    this.formReactivo = new FormGroup({
      name: new FormControl('', [
        Validators.required, Validators.maxLength(50), Validators.pattern(/^\s*[A-Za-zÀ-ÿ]+(?: [A-Za-zÀ-ÿ]+)*\s*$/)
      ]
      ),
      lastname: new FormControl('', [
        Validators.required, Validators.maxLength(50), Validators.pattern(/^\s*[A-Za-zÀ-ÿ]+(?: [A-Za-zÀ-ÿ]+)*\s*$/)
      ]
      ),
      email: new FormControl('', [Validators.required, Validators.email], [this.repeatedEmailValidator()]),
      password: new FormControl('', [
        Validators.required,
        Validators.maxLength(35),
        Validators.minLength(8)
      ], [this.createPasswordValidator()]
      ),
      repeatPassword: new FormControl('', [
        Validators.required,])
    }, { validators: this.passwordMatchValidator }); // validador a nivel de formgroup
  }

  //5. VALIDADORES

  //Validar que el email no exista ya en la base de datos (validación async)
  private repeatedEmailValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }

      const email = control.value;
      return this.userService.checkEmailRepeated(email).pipe(
        map(boolean => {
          if (boolean == true) {
            return {
              repeatedEmailError: true
            };
          } else {
            return null;
          }
        }),
        catchError((error) => {
          console.error('Error validating the email', error);
          return of(null);
        })
      );

    }
  }

  //Validar que la contraseña sea compleja
  private createPasswordValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }

      const password = control.value;

      //Al menos una mayúscula
      if (!/[A-Z]/.test(password)) {
        return of({
          AtLeastOneMayus: true
        });
      }

      // Al menos una letra minúscula
      if (!/[a-z]/.test(password)) {
        return of({
          AtLeastOneMinus: true
        });
      }

      // Al menos 1 número
      if (!/[0-9]/.test(password)) {
        return of({
          AtLeastOneNumber: true
        });
      }

      // 5. Al menos 1 carácter especial
      if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
        return of({
          AtLeastOneEspecialCarac: true
        });
      }

      // 6. Validar espacios en blanco
      if (/\s/.test(password)) {
        return of({
          WhiteSpaceError: true
        });
      }

      // Si todo está bien, retorna un Observable null
      return of(null);

    }
  }


  //Validar que coincidan ambos campos de contraseñas
  passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const form = control as FormGroup;
    const password = form.get("password")?.value;
    const repeatPassword = form.get('repeatPassword')?.value;

    if (password === repeatPassword) {
      return null; // Las contraseñas coinciden: NO hay error
    } else {
      return { passwordMismatch: true }; // Contraseñas NO coinciden: hay error
    }
  }

  //6. MÉTODOS PÚBLICOS (interacción con la vista)
  get isFormInvalid(): boolean {
    return this.formReactivo.invalid;
  }

  //7. MÉTODO PRINCIPAL DE SUBMIT
  onSubmitForm() {
    this.isLoading = true;
    const formValue = this.formReactivo.value;

    const userPost: UserPost = {
      name: formValue.name,
      lastname: formValue.lastname,
      email: formValue.email,
      password: formValue.password,
      roleNames: ['ROLE_ENCARGADO']
    }

   this.userService.postUser(userPost).subscribe({
      next: () => {
        this.isLoading = false;
        this.handleLoginSuccess();
      },
      error: (error: any) => {
        this.isLoading = false;
        console.log(error)
        this.handleLoginError();
      }
    })
  }


  //8. MÉTODOS PRIVADOS (lógica interna)
  
    // Desplegamos swal de éxito
    private handleLoginSuccess(): void {
      Swal.fire({
        title: 'Te has registrado con éxito!',
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
      Swal.fire({
        title: 'Error',
        text: 'Algo salió mal, intentalo más tarde por favor',
        icon: 'error',
        confirmButtonText: 'Cerrar'
      });
    }

}
