import { Component, Input } from '@angular/core';
import { SaleResponse } from '../../models/sale';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { PricePipe } from '../../pipes/price.pipe';
import { LicensePlatePipe } from '../../pipes/license-plate.pipe';
import { KilometersPipe } from '../../pipes/km.pipe';

@Component({
  selector: 'app-modal-info-sale',
  standalone: true,
  imports: [CommonModule, PricePipe, LicensePlatePipe, KilometersPipe],
  templateUrl: './modal-info-sale.component.html',
  styleUrl: './modal-info-sale.component.css'
})
export class ModalInfoSaleComponent {
  @Input() saleModel!: SaleResponse;

  constructor(public activeModal: NgbActiveModal) { }

  get clientFullName(): string {
    const c = this.saleModel?.client;
    return c ? `${c.name} ${c.lastName}` : '';
  }

  /** Acción del botón aceptar (opcional) */
  onConfirm(): void {
    // ejemplo: devolver un resultado al componente padre
    this.activeModal.close({ action: 'confirm', saleId: this.saleModel.id });
  }

}
