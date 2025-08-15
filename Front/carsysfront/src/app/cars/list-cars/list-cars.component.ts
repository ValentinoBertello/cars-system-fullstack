import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Brand, CarResponse, Model, UpdateCarDto } from '../../models/car';
import { DetailsCarService } from '../../services/car/details-car.service';
import { CarService } from '../../services/car/car.service';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { NgxCurrencyDirective } from 'ngx-currency';
import Swal from 'sweetalert2';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalInfoCarComponent } from '../modal-info-car/modal-info-car.component';
import { ModalSaleCarComponent } from '../modal-sale-car/modal-sale-car.component';
import { LicensePlatePipe } from '../../pipes/license-plate.pipe';
import { PricePipe } from '../../pipes/price.pipe';
import { KilometersPipe } from '../../pipes/km.pipe';

@Component({
  selector: 'app-list-cars',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, NgxCurrencyDirective, LicensePlatePipe,
    PricePipe, KilometersPipe
  ],
  templateUrl: './list-cars.component.html',
  styleUrl: './list-cars.component.css'
})
export class ListCarsComponent {

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   1. PROPIEDADES PÚBLICAS
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  public isLoading = false;
  public selectBrands: Brand[] = [];
  public selectModels: Model[] = [];
  public selectedBrand: string = "";
  public selectedModel: string = '';
  public licensePlateInput: string = "";
  public filteredCars: CarResponse[] = [];
  public sortField = "registrationDate";
  sortDirection: 'asc' | 'desc' = 'desc';

  // Paginación
  public currentPage = 0;
  public pageSize = 5;
  public totalPages = 0;
  public hasNext = false;
  public hasPrev = false;

  // Edición en linea
  public editingCarId: number | null = null;
  public editingBasePrice!: number;
  public editingMileage!: number;

  // Subject para debounce de búsqueda por patente
  private licensePlateSubject = new Subject<string>();

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   2. INYECCIÓN DE DEPENDENCIAS
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  private readonly detailsCarService = inject(DetailsCarService);
  private readonly carService = inject(CarService);

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   3. CONSTRUCTOR
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  constructor(private modal: NgbModal, private router: Router) {
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   4. AL INICIAR EL COMPONENTE
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  ngOnInit(): void {
    this.loadBrands();

    //Subscribimos el subject de la patente con debounce
    this.licensePlateSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(value => {
      this.licensePlateInput = value;
      this.applyFilters();
    });

    this.adjustPageSize();
    this.applyFilters();
  }

  // AL CAMBIAR EL TAMAÑO DE LA VENTANA: recalcula cuántos ítems entran por página y vuelve a aplicar los filtros
  @HostListener('window:resize')
  onResize(): void {
    this.adjustPageSize();
    this.applyFilters(0);
  }


  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   5. MÉTODOS PÚBLICOS (interacción con la vista)
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
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

  // Maneja el cambio en marca
  onBrandChange(value: string) {
    this.selectedBrand = value;
    this.loadModels(value);
    this.selectedModel = '';
    this.applyFilters(0);
  }

  // Maneja el cambio en modelo
  onModelChange(value: string) {
    this.selectedModel = value;
    this.applyFilters(0);
  }

  /** Lógica para ordenar por campo y dirección */
  onSortFieldChange(field: string): void {
    this.sortField = field;
    this.applyFilters(0);
  }

  /** Invierte la dirección de orden */
  toggleSortDirection(): void {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.applyFilters(0);
  }

  /** Avanza a la siguiente página si existe */
  goNext(): void {
    if (this.hasNext) {
      this.applyFilters(this.currentPage + 1);
    }
  }

  /** Retrocede a la página anterior si existe */
  goPrev(): void {
    if (this.hasPrev) {
      this.applyFilters(this.currentPage - 1);
    }
  }

  /** Abre el modal con información detallada del auto */
  openInfoModal(car: CarResponse): void {
    const modalRef = this.modal.open(ModalInfoCarComponent, {
      size: 'lg',
      keyboard: false
    });
    modalRef.componentInstance.carModel = car;
  }

   /** Abre el modal para vender el auto */
  openSaleModal(car: CarResponse): void {
    const modalRef = this.modal.open(ModalSaleCarComponent, {
      size: 'lg',
      keyboard: false
    });
    modalRef.componentInstance.carModel = car;

    // Nos suscribimos al event emitter del modal
    modalRef.componentInstance.saleCompleted.subscribe( () => {
      // Esto corre cuando el modal hace emit()
      this.router.navigate(['/sale/list']);
      modalRef.close(); 
    } );
  }

  /** Inicia edición inline para un auto */
  startEditing(car: CarResponse): void {
    this.editingCarId = car.id;
    this.editingBasePrice = car.basePrice;
    this.editingMileage = car.mileage;
  }

  /** Cancela la edición inline */
  cancelEditing(): void {
    this.editingCarId = null;
  }

  /** Aplica los cambios editados inline */
  applyEditing(car: CarResponse): void {
    if (this.editingBasePrice < 0 || this.editingMileage < 0) return;

    const dto: UpdateCarDto = {
      id: car.id,
      basePrice: this.editingBasePrice,
      mileage: this.editingMileage
    };

    this.isLoading = true;
    this.carService.updateCar(dto).subscribe({
      next: updated => {
        const idx = this.filteredCars.findIndex(c => c.id === updated.id);
        if (idx >= 0) this.filteredCars[idx] = updated;
        this.cancelEditing();
        Swal.fire({
          title: 'Auto editado exitosamente!',
          icon: 'success',
          timer: 1300,
          timerProgressBar: true,
          showConfirmButton: false
        });
        this.isLoading = false;
      },
      error: () => {
        // gestionar error…
        this.isLoading = false;
      }
    });
  }

  // Maneja el cambio en el select de marcas
  changeModels(event: any) {
    const brandName = event.target.value;
    this.loadModels(brandName);
    this.selectedModel = '';
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   6. MÉTODOS PRIVADOS (lógica interna)
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  /** Handler cuando el usuario escribe en el input de patente */
  onLicensePlateChange(value: string): void {
    this.licensePlateSubject.next(value);
  }

  /** Aplica todos los filtros, orden, paginación y hace la llamada al endpoint*/
  private applyFilters(page: number = 0): void {
    this.isLoading = true;
    this.currentPage = page;

    // Creamos objeto filters con los filtros seleccionados
    const filters: { licensePlate?: string; brand?: string; model?: string } = {};
    if (this.licensePlateInput) filters.licensePlate = this.licensePlateInput;
    if (this.selectedBrand) filters.brand = this.selectedBrand;
    if (this.selectedModel) filters.model = this.selectedModel;

    // Creamos string para aplicar el ordenamiento en la futura url
    const sortParam = `${this.sortField},${this.sortDirection}`;

    // Llamada al endpoint para traer los autos paginados
    this.carService
      .getCarsPageByFilters(filters, this.currentPage, this.pageSize, sortParam)
      .subscribe({
        next: pageData => {
          this.filteredCars = pageData.content;
          this.totalPages = pageData.totalPages;
          this.hasNext = !pageData.last;
          this.hasPrev = !pageData.first;
          this.isLoading = false;
        },
        error: err => {
          console.error('Error al buscar coches:', err);
          this.isLoading = false;
        }
      });
  }

  /** Ajusta el tamaño de página según la altura de la ventana */
  private adjustPageSize(): void {
    const h = window.innerHeight;
    if (h >= 4320) this.pageSize = 24;
    else if (h >= 2880) this.pageSize = 22;
    else if (h >= 2160) this.pageSize = 18;
    else if (h >= 1440) this.pageSize = 15;
    else if (h >= 1100) this.pageSize = 11;
    else if (h >= 920) this.pageSize = 8;
    else if (h >= 880) this.pageSize = 7;
    else if (h >= 798) this.pageSize = 6;
    else if (h >= 720) this.pageSize = 5;
    else this.pageSize = 3;
  }
}
