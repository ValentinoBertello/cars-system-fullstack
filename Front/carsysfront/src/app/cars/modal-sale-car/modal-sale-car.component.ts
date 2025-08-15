import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgxCurrencyDirective } from 'ngx-currency';
import { ClientResponse } from '../../models/client';
import { ClientService } from '../../services/client.service';
import { CarResponse } from '../../models/car';
import { SaleService } from '../../services/sale.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { PostSaleDto, PostSaleWithClientDto } from '../../models/sale';
import { catchError, map, Observable, of } from 'rxjs';
import { LicensePlatePipe } from '../../pipes/license-plate.pipe';
import { PricePipe } from '../../pipes/price.pipe';

@Component({
  selector: 'app-modal-sale-car',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxCurrencyDirective, LicensePlatePipe,
    PricePipe
  ],
  templateUrl: './modal-sale-car.component.html',
  styleUrl: './modal-sale-car.component.css'
})
export class ModalSaleCarComponent {

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   1. PROPIEDADES PÚBLICAS
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  // Auto enviado desde el ListCarsComponent
  @Input() carModel!: CarResponse;
  // Output para refrescar la lista al cerrar
  @Output() saleCompleted = new EventEmitter<void>();

  public isLoading = false;
  public searchButtonTouched = false;
  public isNewClient = false;
  public searchInput: string = "";
  public selectedClientId?: number | null = null;
  public filteredClients: ClientResponse[] = []
  public newClientForm: FormGroup;

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     2. INYECCIÓN DE DEPENDENCIAS
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  private fb = inject(FormBuilder);
  private readonly clientService = inject(ClientService);
  private readonly saleService = inject(SaleService)

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   3. CONSTRUCTOR
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  constructor(public activeModal: NgbActiveModal) {
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
    if (this.searchButtonTouched === false) {
      this.searchButtonTouched = true;
    }
    this.clientService.searchClientsByFilter(this.searchInput).subscribe({
      next: (data) => {
        this.filteredClients = data;
      },
      error: () => {
      }
    });
  }

  // Asignamos el cliente elegido a nuestra variable
  selectClient(client: ClientResponse): void {
    this.selectedClientId = client.id;
  }

  // Cierra el modal sin hacer nada
  cancel(): void {
    this.activeModal.close('Close click');
  }

  // Cambiar entre modos
  toggleNewClient(): void {
    this.isNewClient = !this.isNewClient;
    this.selectedClientId = null;
    this.searchInput = '';
    this.filteredClients = [];
    this.newClientForm.reset();
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   6. MÉTODOS PRINCIPAL SUBMIT
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  confirmSale(): void {
    // Validaciones iniciales
    if (this.isNewClient) {
      if (this.newClientForm.invalid) {
        Swal.fire('Atención', 'Por favor complete correctamente los campos.', 'warning');
        return;
      }
      this.processNewClientSale();
    } else {
      if (this.selectedClientId == null) {
        Swal.fire('Atención', 'Por favor selecciona un cliente.', 'warning');
        return;
      }
      this.processExistingClientSale();
    }
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   7. MÉTODOS PRIVADOS (Lógica interna)
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  // Arma y envía la venta creando un cliente nuevo
  private processNewClientSale(): void {
    this.isLoading = true;

    const dto: PostSaleWithClientDto = {
      carId: this.carModel.id,
      salePrice: this.carModel.basePrice,
      clientName: this.newClientForm.value.clientName,
      clientLastName: this.newClientForm.value.clientLastName,
      clientPhone: this.newClientForm.value.clientPhone,
      clientDni: this.newClientForm.value.clientDni
    };

    this.saleService.saveSaleNewClient(dto).subscribe({
      next: () => this.handleSuccess(),
      error: (err) => this.handleError(err)
    });
  }

  // Arma y envía la venta para un cliente ya existente */
  private processExistingClientSale(): void {
    this.isLoading = true;

    const dto: PostSaleDto = {
      carId: this.carModel.id,
      clientId: this.selectedClientId!,
      salePrice: this.carModel.basePrice
    };

    this.saleService.saveSale(dto).subscribe({
      next: () => this.handleSuccess(),
      error: (err) => this.handleError(err)
    });
  }

  // Muestra el Swal de éxito y emite el evento para el padre
  private handleSuccess(): void {
    this.isLoading = false;
    Swal.fire('¡Éxito!', 'Venta registrada correctamente.', 'success')
      .then(() => this.saleCompleted.emit());
  }

  // Muestra el Swal de error y resetea el loading
  private handleError(error: any): void {
    console.error('Error al registrar venta:', error);
    this.isLoading = false;
    Swal.fire('Error', 'No se pudo completar la venta.', 'error');
  }

}
