import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgxCurrencyDirective } from 'ngx-currency';
import { SaleResponse } from '../../models/sale';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { SaleService } from '../../services/sale.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalInfoSaleComponent } from '../modal-info-sale/modal-info-sale.component';
import { LicensePlatePipe } from '../../pipes/license-plate.pipe';
import { PricePipe } from '../../pipes/price.pipe';

@Component({
  selector: 'app-list-sales',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, LicensePlatePipe, PricePipe],
  templateUrl: './list-sales.component.html',
  styleUrl: './list-sales.component.css'
})
export class ListSalesComponent {

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   1. PROPIEDADES PÚBLICAS
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  public isLoading = false;
  public sinceDateInput: string = '';
  public untilDateInput: string = '';
  public carQueryInput: string = "";
  public clientQueryInput: string = "";
  public filteredSales: SaleResponse[] = [];
  public sortField = "saleDate";
  sortDirection: 'asc' | 'desc' = 'desc';

  // Paginación
  public currentPage = 0;
  public pageSize = 5;
  public totalPages = 0;
  public hasNext = false;
  public hasPrev = false;

  // Subject para debounce de búsquedas
  private carQuerySubject = new Subject<string>();
  private clientQuerySubject = new Subject<string>();

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   2. INYECCIÓN DE DEPENDENCIAS
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  private readonly saleService = inject(SaleService);

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   3. AL INICIAR EL COMPONENTE
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
   constructor(private modal: NgbModal, private router: Router) {
   }

  ngOnInit(): void {
    this.setDefaultDates();
    this.initCarQuerySearch();
    this.initClientQuerySearch();
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
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ **
  /** Lógica para ordenar por campo y dirección */
  onSortFieldChange(field: string): void {
    this.sortField = field;
    this.applyFilters(0);
  }

  // Maneja el cambio en fecha "desde"
  onSinceDateChange(value: string) {
    this.sinceDateInput = value;
    this.applyFilters(0);
  }

  // Maneja el cambio en fecha "hasts"
  onUntilDateChange(value: string) {
    this.untilDateInput = value;
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
    openInfoModal(sale: SaleResponse): void {
      const modalRef = this.modal.open(ModalInfoSaleComponent, {
        size: 'lg',
        keyboard: false
      });
      modalRef.componentInstance.saleModel = sale;
    }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   6. MÉTODOS PRIVADOS (lógica interna)
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ **
    /** Aplica todos los filtros, orden, paginación y hace la llamada al endpoint */
  private applyFilters(page: number = 0): void {
    this.isLoading = true;
    this.currentPage = page;

    // Creamos objeto filters con los filtros seleccionados
    const filters: { sinceDate?: string; untilDate?: string; clientQuery?: string; carQuery?: string; } = {};
    if (this.sinceDateInput) filters.sinceDate = this.sinceDateInput;
    if (this.untilDateInput) filters.untilDate = this.untilDateInput;
    if (this.clientQueryInput) filters.clientQuery = this.clientQueryInput;
    if (this.carQueryInput) filters.carQuery = this.carQueryInput;

    // Creamos el string para aplicar el ordenamiento en la futura url
    const sortParam = `${this.sortField},${this.sortDirection}`;

    // Llamada al endpoint para traer las ventas paginadas
    this.saleService
      .searchSalesByFilters(filters, this.currentPage, this.pageSize, sortParam)
      .subscribe({
        next: pageData => {
          this.filteredSales = pageData.content;
          this.totalPages = pageData.totalPages;
          this.hasNext = !pageData.last;
          this.hasPrev = !pageData.first;
          this.isLoading = false;
        }
      });
  }

  /** Suscribe y aplica debounce al input de patente */
  private initCarQuerySearch(): void {
    this.carQuerySubject.pipe(
      debounceTime(300),        // 300 ms de inactividad antes de asignar el valor a la variable
      distinctUntilChanged()    // solo si cambió el valor
    ).subscribe(value => {
      this.carQueryInput = value;
      this.applyFilters(0);
    });
  }

  /** Suscribe y aplica debounce al input de DNI, nombre o apellido */
  private initClientQuerySearch(): void {
    this.clientQuerySubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(value => {
      this.clientQueryInput = value;
      this.applyFilters(0);
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

  /** Handler cuando el usuario escribe en el input de patente */
  onCarQueryChange(value: string): void {
    this.carQuerySubject.next(value);
  }

  /** Handler cuando el usuario escribe en el input de client */
  onClientQueryChange(value: string): void {
    this.clientQuerySubject.next(value);
  }

  /**
   * Establece las fechas iniciales por defecto para un filtro o formulario.
  */
  private setDefaultDates() {
    const today = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);

    // Generás las cadenas en base a la fecha local, sin pasar por UTC
    this.sinceDateInput = this.formatDateLocal(oneMonthAgo);
    this.untilDateInput = this.formatDateLocal(today);
  }

  /*
  * Convierte un objeto Date en una cadena de texto con formato `yyyy-MM-dd`
  */
  private formatDateLocal(date: Date): string {
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 0..11 → +1
    const day = date.getDate();      // 1..31

    // Asegurarse de dos dígitos en mes y día
    const mm = month < 10 ? '0' + month : month.toString();
    const dd = day < 10 ? '0' + day : day.toString();

    return `${year}-${mm}-${dd}`; // formato "2025-04-22"
  }
}
