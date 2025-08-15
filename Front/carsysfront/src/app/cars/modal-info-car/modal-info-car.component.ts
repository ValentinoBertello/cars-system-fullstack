import { Component, Input } from '@angular/core';
import { CarResponse } from '../../models/car';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { LicensePlatePipe } from '../../pipes/license-plate.pipe';
import { PricePipe } from '../../pipes/price.pipe';
import { KilometersPipe } from '../../pipes/km.pipe';


@Component({
  selector: 'app-modal-info-car',
  standalone: true,
  imports: [CommonModule, LicensePlatePipe, PricePipe, KilometersPipe],
  templateUrl: './modal-info-car.component.html',
  styleUrl: './modal-info-car.component.css'
})
export class ModalInfoCarComponent {

  // Auto enviado desde el ListCarsComponent
  @Input() carModel!: CarResponse;

  constructor(
    public activeModal: NgbActiveModal
  ) { }

  ngOnInit(): void {
  }
  
  closeModal(): void {
    this.activeModal.close('Close click');
  }
}
