import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { DetailsCarService } from '../../services/car/details-car.service';
import { Brand, CarPost, Model } from '../../models/car';
import Swal from 'sweetalert2';
import { CarService } from '../../services/car/car.service';
import { AuthService } from '../../services/users/auth.service';
import { NgxCurrencyDirective } from "ngx-currency";
import { catchError, map, Observable, of } from 'rxjs';

@Component({
  selector: 'app-create-car',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxCurrencyDirective],
  templateUrl: './create-car.component.html',
  styleUrl: './create-car.component.css'
})
export class CreateCarComponent {

  //1. PROPIEDADES PÚBLICAS
  public isLoading = false;
  selectBrands: Brand[] = [];
  selectModels: Model[] = [];
  colorsSelect: string[] = [
    'Azul',
    'Blanco',
    'Gris',
    'Negro',
    'Rojo'
  ];


  //2. FORMULARIO REACTIVO
  formReactivo: FormGroup;

  //3. INYECCIÓN DE DEPENDENCIAS
  private readonly detailsCarService = inject(DetailsCarService);
  private readonly carService = inject(CarService);
  private readonly authService = inject(AuthService);

  //4. CONSTRUCTOR
  constructor(private router: Router) {
    // Inicializamos el formulario
    this.formReactivo = new FormGroup({
      // marca el auto
      brandId: new FormControl('', [
        Validators.required
      ]
      ),
      // Modelo el auto
      modelId: new FormControl('', [
        Validators.required
      ]
      ),
      // Patente
      licensePlate: new FormControl('', [
        Validators.required,
        Validators.pattern((/^([A-Za-z]{2}\d{3}[A-Za-z]{2}|[A-Za-z]{3}\d{3})$/))
      ], [this.repeatedLicencePlateValidator()]
      ),
      // Año
      year: new FormControl('', [
        Validators.required,
        Validators.min(1800),
        Validators.max(2100)
      ]
      ),
      // Kilometraje
      mileage: new FormControl('', [
        Validators.required,
        Validators.min(0),
        Validators.max(999999)
      ]
      ),
      // Color
      color: new FormControl('', [
        Validators.required
      ]
      ),
      // Precio
      basePrice: new FormControl('', [
        Validators.required,
        Validators.min(1),
        Validators.max(190000000)
      ]
      )
    });
  }

  //5. VALIDADORES
  // Validamos que la patente no sea repetida (async)
  repeatedLicencePlateValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }
      const licensePlate = control.value.toUpperCase();

      return this.detailsCarService.existsLicensePlate(licensePlate).pipe(

        map(bool => {
          if (bool == true) {
            return {
              repeatedLicensePlateError: true
            };
          } else {
            return null;
          }
        }),

        catchError((error) => {
          console.error('Error validating the license plate', error);
          return of(null);
        })
      );
    };
  }

  //6. MÉTODOS PÚBLICOS (interacción con la vista)
  ngOnInit(): void {
    this.loadBrands();
  }

  // Cargamos el array de marcas
  loadBrands() {
    this.detailsCarService.getAllBrands().subscribe({
      next: (data) => {
        this.selectBrands = data;
      },
      error: () => {
      }
    });
  }

  // Cargamos modelos segun marca seleccionada
  loadModels(brandName: string) {
    this.detailsCarService.getAllModels().subscribe({
      next: (data) => {
        this.selectModels = data.filter(item => item.brandName == brandName);
      },
      error: () => {
      }
    });
  }

  // Maneja el cambio en el select de marcas
  changeModels(event: any) {
    const brandName = event.target.value;
    this.loadModels(brandName);
    this.formReactivo.get('modelId')?.setValue('');
  }

  // Limpiamos form
  clean() {
    this.formReactivo.setValue({
      brandId: '',
      modelId: '',
      licensePlate: '',
      year: '',
      mileage: 0,
      color: '',
      basePrice: 0
    });

    this.formReactivo.markAsPristine();
    this.formReactivo.markAsUntouched();

    this.selectModels = [];
  }

  //7. MÉTODO PRINCIPAL SUBMIT
  onSubmitForm() {
    this.isLoading = true;
    const formValue = this.formReactivo.value;

    const carPost: CarPost = {
      userEmail: this.authService.getCurrentUserEmail(),
      modelId: Number(formValue.modelId),
      licensePlate: formValue.licensePlate.trim().toUpperCase(),
      year: Number(formValue.year),
      color: formValue.color,
      basePrice: Number(formValue.basePrice),
      mileage: Number(formValue.mileage)
    }


    this.carService.saveCar(carPost).subscribe({
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
      title: 'Auto guardado exitosamente!',
      icon: 'success',
      timer: 1300,
      timerProgressBar: true,
      showConfirmButton: false
    }).then(() => {
      this.router.navigate(['/car/list']);
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
