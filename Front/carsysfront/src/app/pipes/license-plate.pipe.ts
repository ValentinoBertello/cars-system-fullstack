import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'licensePlate',
  standalone: true
})
export class LicensePlatePipe implements PipeTransform {

  transform(value: string): string {
    if (!value) return '';

    const lp = value.toUpperCase().trim();

    if (lp.length === 6) {
      // Formato ABC123 → ABC 123
      return lp.slice(0, 3) + ' ' + lp.slice(3);
    } else if (lp.length === 7) {
      // Formato AA123VB → AA 123 VB
      return lp.slice(0, 2) + ' ' + lp.slice(2, 5) + ' ' + lp.slice(5);
    } else {
      return lp;
    }
  }
}