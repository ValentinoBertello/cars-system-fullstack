import { inject, Injectable } from '@angular/core';
import { GlobalService } from './global.service';
import { HttpClient } from '@angular/common/http';
import { PostSaleDto, PostSaleWithClientDto, SaleResponse } from '../models/sale';
import { Observable } from 'rxjs';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class SaleService {

  private readonly globalUrls = inject(GlobalService);
  private readonly urlBase = this.globalUrls.apiUrlLocalHost8085;
  private readonly http = inject(HttpClient);

  // Guardamos nueva venta con un NUEVO cliente
  saveSaleNewClient(saleDtoNewClient: PostSaleWithClientDto): Observable<SaleResponse> {
    return this.http.post<SaleResponse>(this.urlBase + "/sales/register/with-client", saleDtoNewClient);
  }

  // Guardamos nueva venta con un cliente existente
  saveSale(saleDto: PostSaleDto): Observable<SaleResponse> {
    return this.http.post<SaleResponse>(this.urlBase + "/sales/register", saleDto);
  }

  // Busca ventas según filtros opcionales: fecha "desde" y fecha "hasta", dni de cliente y
  // patente de auto vendido.
  searchSalesByFilters(
    filters: { sinceDate?: string; untilDate?: string; clientQuery?: string; carQuery?: string; },
    page: number,
    size: number = 10,
    sort?: string
  ): Observable<Page<SaleResponse>> {
    // Comenzamos con los parámetros de paginación obligatorios
    let query = `?page=${page}&size=${size}`;

    // Si tenemos rango de fechas completo, lo agregamos
    if (filters.sinceDate && filters.untilDate) {
      query += `&sinceDate=${filters.sinceDate}&untilDate=${filters.untilDate}`
    }

    // Filtro adicional por DNI de cliente
    if (filters.clientQuery) {
      query += `&clientQuery=${filters.clientQuery}`
    }

    // Filtro adicional por patente, modelo o marca de auto
    if (filters.carQuery) {
      query += `&carQuery=${filters.carQuery}`;
    }

    // Si nos pasaron un sort lo incluimos también
    if (sort) {
      query += `&sort=${sort}`;
    }

    // Construimos la URL completa y lanzamos la petición GET
    const url = `${this.urlBase}/sales/search${query}`;
    return this.http.get<Page<SaleResponse>>(url);
  }

  // Traemos todas ventas asociadas a un cliente en específico
  searchSalesByDniClient(dniClient: string): Observable<SaleResponse[]> {
    return this.http.get<SaleResponse[]>(this.urlBase + "/sales/client/" + dniClient);
  }

}
