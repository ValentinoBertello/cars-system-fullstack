import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { ClientResponse } from '../../models/client';
import { SaleResponse } from '../../models/sale';
import { SaleService } from '../../services/sale.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LicensePlatePipe } from '../../pipes/license-plate.pipe';
import { PricePipe } from '../../pipes/price.pipe';

@Component({
  selector: 'app-modal-client-sales',
  standalone: true,
  imports: [CommonModule, LicensePlatePipe, PricePipe],
  templateUrl: './modal-client-sales.component.html',
  styleUrl: './modal-client-sales.component.css'
})
export class ModalClientSalesComponent {

  // Auto enviado desde el ListClientsComponent
  @Input() clientModel!: ClientResponse;

  // Ventas asociadas a un cliente determinado
  public sales: SaleResponse[] = [];

  private readonly saleService = inject(SaleService);

  constructor(
    public activeModal: NgbActiveModal
  ) { }

  ngOnInit(): void {
    this.loadSales();
  }

  loadSales() {
    // Llamada al endpoint para traer las ventas del cliente
    this.saleService
      .searchSalesByDniClient(this.clientModel.dni)
      .subscribe({
        next: sales => {
          this.sales = sales;
        },
        error: err => {
          console.error('Error al buscar las ventas:', err);
        }
      });
  }

  closeModal(): void {
    this.activeModal.close('Close click');
  }

  /** Formatea un número como kilómetros con separadores de miles */
  formatKm(km: number): string {
    return km.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  }
}
