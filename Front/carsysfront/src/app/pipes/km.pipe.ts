// kilometers.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'kilometers',
  standalone: true
})
export class KilometersPipe implements PipeTransform {

  transform(value: number | string | null | undefined): string {
    if (value === null || value === undefined || value === '') return '';

    const raw = typeof value === 'number' ? value.toString() : value.toString().trim();

    // Manejo de signo negativo
    const negative = raw.startsWith('-');
    const unsigned = negative ? raw.slice(1) : raw;

    // Separar parte entera y decimales (acepta '.' o ',' como separador decimal)
    const [intPart, decPart] = unsigned.split(/[.,]/);

    // Insertar separador de miles '.'
    const intFormatted = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

    // Usamos coma como separador decimal (estilo latino)
    const formatted = decPart !== undefined ? `${intFormatted},${decPart}` : intFormatted;

    return `${negative ? '-' : ''}${formatted}`;
  }
}