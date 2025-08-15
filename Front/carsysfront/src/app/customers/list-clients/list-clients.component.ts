import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ClientResponse } from '../../models/client';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { ClientService } from '../../services/client.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalClientSalesComponent } from '../modal-client-sales/modal-client-sales.component';

@Component({
  selector: 'app-list-clients',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './list-clients.component.html',
  styleUrl: './list-clients.component.css'
})
export class ListClientsComponent {

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     1. PROPIEDADES PÚBLICAS
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  public isLoading = false;
  public clientQueryInput: string = "";
  public filteredClients: ClientResponse[] = [];
  public sortField = "registrationDate";
  sortDirection: 'asc' | 'desc' = 'desc';

  // Paginación
  public currentPage = 0;
  public pageSize = 5;
  public totalPages = 0;
  public hasNext = false;
  public hasPrev = false;

  // Subject para debounce de búsquedas
  private clientQuerySubject = new Subject<string>();

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     2. INYECCIÓN DE DEPENDENCIAS
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  private readonly clientService = inject(ClientService);

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   3. AL INICIAR EL COMPONENTE
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
  constructor(private modal: NgbModal, private router: Router) {
  }

  ngOnInit(): void {
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
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ **/
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

  /** Abre el modal con información detallada de las ventas del cliente */
  openInfoModal(client: ClientResponse): void {
    const modalRef = this.modal.open(ModalClientSalesComponent, {
      size: 'lg',
      keyboard: false
    });
    modalRef.componentInstance.clientModel = client;
  }

  /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   6. MÉTODOS PRIVADOS (lógica interna)
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ **/
  /** Aplica todos los filtros, orden, paginación y hace la llamada al endpoint */
  private applyFilters(page: number = 0): void {
    this.isLoading = true;
    this.currentPage = page;

    // Creamos objeto filters con los filtros seleccionados
    const filters: { clientQuery?: string; } = {};
    if (this.clientQueryInput) filters.clientQuery = this.clientQueryInput;

    // Creamos el string para aplicar el ordenamiento en la futura url
    const sortParam = `${this.sortField},${this.sortDirection}`;

    // Llamada al endpoint para traer los clientes paginados
    this.clientService
      .searchClientsPageByFilter(filters, this.currentPage, this.pageSize, sortParam)
      .subscribe({
        next: pageData => {
          this.filteredClients = pageData.content;
          this.totalPages = pageData.totalPages;
          this.hasNext = !pageData.last;
          this.hasPrev = !pageData.first;
          this.isLoading = false;
        },
        error: err => {
          console.error('Error al buscar los clientes:', err);
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

  /** Handler cuando el usuario escribe en el input de client */
  onClientQueryChange(value: string): void {
    this.clientQuerySubject.next(value);
  }
}
