import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxCurrencyDirective } from 'ngx-currency';
import { ClientResponse } from '../../models/client';
import { CarResponse } from '../../models/car';
import { ClientService } from '../../services/client.service';
import { SaleService } from '../../services/sale.service';
import { CarService } from '../../services/car/car.service';
import { catchError, map, Observable, of } from 'rxjs';
import Swal from 'sweetalert2';
import { PostSaleDto, PostSaleWithClientDto } from '../../models/sale';
import { LicensePlatePipe } from '../../pipes/license-plate.pipe';
import { PricePipe } from '../../pipes/price.pipe';

@Component({
  selector: 'app-create-sale',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxCurrencyDirective, LicensePlatePipe, PricePipe],
  templateUrl: './create-sale.component.html',
  styleUrl: './create-sale.component.css'
})
export class CreateSaleComponent {

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   1. PROPIEDADES PÚBLICAS
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  public isLoading: boolean = false;
  public isNewClient: boolean = false;
  public clientSearchInput: string = "";
  public searchClientButtonTouched = false;
  public carSearchInput: string = "";
  public searchCarButtonTouched = false;
  public selectedClientId?: number | null = null;
  public filteredClients: ClientResponse[] = []
  public filteredCars: CarResponse[] = []
  public newClientForm: FormGroup;
  public selectedCar: CarResponse = {
    id: 0,
    licensePlate: '',
    modelName: '',
    brandName: '',
    userId: 0,
    userEmail: '',
    year: 0,
    color: '',
    basePrice: 0,
    mileage: 0,
    status: '',
    registrationDate: ''
  };
  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   2. INYECCIÓN DE DEPENDENCIAS
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  private fb = inject(FormBuilder);
  private readonly clientService = inject(ClientService);
  private readonly carService = inject(CarService);
  private readonly saleService = inject(SaleService);

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     3. CONSTRUCTOR
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  constructor(private router: Router) {
    // INICIALIZAR newClientForm
    this.newClientForm = this.fb.group({
      clientName: ['', [Validators.required, Validators.maxLength(50)]],
      clientLastName: ['', [Validators.required, Validators.maxLength(50)]],
      clientPhone: [
        '',
        [Validators.required],       // validadores SÍNCRONOS
        [this.repeatedPhoneValidator()] // validadores ASÍNCRONOS
      ],
      clientDni: [
        '',
        [Validators.required, Validators.pattern(/^[0-9]{7,8}$/)],// validadores SÍNCRONOS
        [this.repeatedDniValidator()] // validadores ASÍNCRONOS
      ]
    });
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 4. VALIDADORES
 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  // Validamos que el celular no sea repetido (async)
  repeatedPhoneValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }
      const phone = control.value;

      return this.clientService.existsPhone(phone).pipe(

        map(bool => {
          if (bool == true) {
            return {
              repeatedPhoneError: true
            };
          } else {
            return null;
          }
        }),

        catchError((error) => {
          console.error('Error validating the phone', error);
          return of(null);
        })
      );
    };
  }

  // Validamos que el dni no sea repetido (async)
  repeatedDniValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }
      const dni = control.value;

      return this.clientService.existsDni(dni).pipe(

        map(bool => {
          if (bool == true) {
            return {
              repeatedDniError: true
            };
          } else {
            return null;
          }
        }),

        catchError((error) => {
          console.error('Error validating the dni', error);
          return of(null);
        })
      );
    };
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  5. MÉTODOS PÚBLICOS (interacción con la vista)
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  // Buscamos clientes segun el contenido del "searchInput"
  searchClients() {
    if (this.searchClientButtonTouched === false) {
      this.searchClientButtonTouched = true;
    }
    this.clientService.searchClientsByFilter(this.clientSearchInput).subscribe({
      next: (data) => {
        this.filteredClients = data;
      },
      error: () => {
      }
    });
  }

  // Buscamos autos segun el contenido del "searchInput"
  searchCars() {
    if (this.searchCarButtonTouched === false) {
      this.searchCarButtonTouched = true;
    }
    this.carService.getCarsByFilters(this.carSearchInput).subscribe({
      next: (data) => {
        this.filteredCars = data;
      },
      error: () => {
      }
    });
  }

  // Asignamos el cliente elegido a nuestra variable
  selectClient(client: ClientResponse): void {
    this.selectedClientId = client.id;
  }

  // Asignamos el cliente elejido a nuestra variable
  selectCar(car: CarResponse): void {
    this.selectedCar = car;
  }

  // Cambiar entre modo de creación de cliente y o de selección de cliente
  toggleNewClient(): void {
    this.isNewClient = !this.isNewClient;
    this.selectedClientId = null;
    this.clientSearchInput = '';
    this.filteredClients = [];
    this.newClientForm.reset();
  }

  goToCreateCar() {
    this.router.navigate(['/car/create']);
  }

  cancel() {
    // cancelar cualquier loading en curso
    this.isLoading = false;

    // reset selected car (mismo shape que tu interfaz CarResponse)
    this.selectedCar = {
      id: 0,
      licensePlate: '',
      modelName: '',
      brandName: '',
      userId: 0,
      userEmail: '',
      year: 0,
      color: '',
      basePrice: 0,
      mileage: 0,
      status: '',
      registrationDate: ''
    };

    // limpiar búsqueda y resultados de autos
    this.carSearchInput = '';
    this.filteredCars = [];
    this.searchCarButtonTouched = false; // si usás esta flag en la vista

    // deseleccionar y limpiar clientes + búsqueda
    this.selectedClientId = null;
    this.clientSearchInput = '';
    this.filteredClients = [];
    this.searchClientButtonTouched = false; // si usás esta flag en la vista

    // si están en modo "crear cliente", vaciar el form reactivo
    if (this.newClientForm) {
      this.newClientForm.reset({
        clientName: '',
        clientLastName: '',
        clientPhone: '',
        clientDni: ''
      });
      this.newClientForm.markAsPristine();
      this.newClientForm.markAsUntouched();
    }
  }
  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  6. MÉTODOS PRINCIPAL SUBMIT
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  confirmSale(): void {
    if (this.selectedCar.id === 0) {
      Swal.fire('Atención', 'Por favor seleccione un auto para vender.', 'warning');
      return;
    }
    // Validaciones iniciales
    if (this.isNewClient) {
      if (this.newClientForm.invalid) {
        Swal.fire('Atención', 'Por favor complete correctamente los campos.', 'warning');
        return;
      }

      // Pedimos confirmación de datos en un Swal
      const info = this.getSuccesfullInfo();
      Swal.fire({
        title: '<strong>Confirmar venta</strong>',
        html: info.confirmHtml,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí, confirmar',
        cancelButtonText: 'Cancelar',
        reverseButtons: true,
        customClass: {
          popup: 'custom-swal-popup',
          title: 'custom-swal-title'
        }
      }).then(result => {
        if (result.isConfirmed) {
          this.processNewClientSale();
        }
      });
    } else {
      if (this.selectedClientId == null) {
        Swal.fire('Atención', 'Por favor selecciona un cliente.', 'warning');
        return;
      }

      // Pedimos confirmación de datos en un Swal
      const info = this.getSuccesfullInfo();
      Swal.fire({
        title: '<strong>Confirmar venta</strong>',
        html: info.confirmHtml,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí, confirmar',
        cancelButtonText: 'Cancelar',
        reverseButtons: true,
        customClass: {
          popup: 'custom-swal-popup',
          title: 'custom-swal-title'
        }
      }).then(result => {
        if (result.isConfirmed) {
          this.processExistingClientSale();
        }
      });
    }
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     7. MÉTODOS PRIVADOS (Lógica interna)
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  // Arma y envía la venta creando un cliente nuevo
  private processNewClientSale(): void {
    this.isLoading = true;

    const dto: PostSaleWithClientDto = {
      carId: this.selectedCar.id,
      salePrice: this.selectedCar.basePrice,
      clientName: this.newClientForm.value.clientName,
      clientLastName: this.newClientForm.value.clientLastName,
      clientPhone: this.newClientForm.value.clientPhone,
      clientDni: this.newClientForm.value.clientDni
    };

    this.saleService.saveSaleNewClient(dto).subscribe({
      next: () => this.handleSwalSuccess(),
      error: (err) => this.handleSwalError(err)
    });
  }

  // Arma y envía la venta para un cliente ya existente */
  private processExistingClientSale(): void {
    this.isLoading = true;

    const dto: PostSaleDto = {
      carId: this.selectedCar.id,
      clientId: this.selectedClientId!,
      salePrice: this.selectedCar.basePrice
    };

    this.saleService.saveSale(dto).subscribe({
      next: () => this.handleSwalSuccess(),
      error: (err) => this.handleSwalError(err)
    });
  }

  // Muestra el Swal de éxito y emite el evento para el padre
  private handleSwalSuccess(): void {
    // DATOS PARA EL SWAL
    this.isLoading = false;
    Swal.fire({
      title: 'Venta registrada!',
      icon: 'success',
      timer: 1300,
      timerProgressBar: true,
      showConfirmButton: false
    }).then(() => {
      this.router.navigate(['/sale/list']);
    });
  }

  // Muestra el Swal de error y resetea el loading
  private handleSwalError(error: any): void {
    console.error('Error al registrar venta:', error);
    this.isLoading = false;
    Swal.fire('Error', 'No se pudo completar la venta.', 'error');
  }

  getSuccesfullInfo() {
    const carDesc = `${this.selectedCar.brandName} ${this.selectedCar.modelName} (${this.selectedCar.licensePlate})`;
    const salePrice = this.selectedCar.basePrice ?? 0;
    const priceFormatted = new Intl.NumberFormat('es-AR', {
      style: 'currency',
      currency: 'ARS',
      maximumFractionDigits: 0
    }).format(salePrice);

    let clientFullName = '';
    if (this.isNewClient) {
      clientFullName = `${this.newClientForm.value.clientName ?? ''} ${this.newClientForm.value.clientLastName ?? ''}`.trim();
    } else {
      const found = this.filteredClients?.find(c => c.id === this.selectedClientId);
      clientFullName = found ? `${found.name} ${found.lastName}` : 'cliente seleccionado';
    }

    const confirmHtml = `
    ¿Estás seguro de vender el siguiente auto?<br><br>
    <b>${carDesc}</b><br><br>
    a un precio de <b style="color: #2196F3;">${priceFormatted}</b><br><br>
    al cliente <b>${clientFullName}</b>?
  `;

    return {
      confirmHtml
    };
  }
}
