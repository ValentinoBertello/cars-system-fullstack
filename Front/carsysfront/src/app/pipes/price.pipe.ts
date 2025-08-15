// price.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'price',
  standalone: true
})
export class PricePipe implements PipeTransform {

  transform(value: number | string | null | undefined, currencySymbol: string = '$'): string {
    // Permitimos 0 como valor válido; sólo descartamos null/undefined/empty string
    if (value === null || value === undefined || value === '') return '';

    const raw = typeof value === 'number' ? value.toString() : value.toString().trim();

    // Manejo de signo negativo
    const negative = raw.startsWith('-');
    const unsigned = negative ? raw.slice(1) : raw;

    // Separar parte entera y decimales (acepta tanto '.' como ',' como separador decimal)
    const [intPart, decPart] = unsigned.split(/[.,]/);

    // Insertar separador de miles '.' en la parte entera
    const intFormatted = intPart.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

    // Usamos coma como separador decimal (estilo latino). Si prefieres punto, cambiá ',' por '.'
    const formatted = decPart !== undefined ? `${intFormatted},${decPart}` : intFormatted;

    return `${currencySymbol}${negative ? '-' : ''}${formatted}`;
  }
  
}