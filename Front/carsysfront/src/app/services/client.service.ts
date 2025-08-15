import { inject, Injectable } from '@angular/core';
import { GlobalService } from './global.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientResponse } from '../models/client';
import { Page } from '../models/page';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private readonly globalUrls = inject(GlobalService);
  private readonly urlBase = this.globalUrls.apiUrlLocalHost8085;
  private readonly http = inject(HttpClient);

  // Buscamos CLIENTES según el filtro enviado.
  searchClientsByFilter(filter: string): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(this.urlBase + "/clients/search/" + filter);
  }

  // Preguntamos si existe un cliente con ese numero de celular
  existsPhone(phone: string): Observable<boolean> {
    return this.http.get<boolean>(this.urlBase + "/clients/exists/phone/" + phone);
  }

  // Preguntamos si existe un cliente con ese dni
  existsDni(dni: string): Observable<boolean> {
    return this.http.get<boolean>(this.urlBase + "/clients/exists/dni/" + dni);
  }

  // Buscamos CLIENTES PAGINADOS según el filtro enviado (nombre, apellido o dni).
  searchClientsPageByFilter(
    filter: {  clientQuery?: string; },
    page: number,
    size: number = 10,
    sort?: string): Observable<Page<ClientResponse>> {

    // Comenzamos con los parámetros de paginación obligatorios
    let query = `?page=${page}&size=${size}`;

    // Añadimos filtro de clientQuery (nombre, apellido o dni)
    if (filter.clientQuery) {
      query += `&filter=${filter.clientQuery}`;
    }

    if (sort) {
      query += `&sort=${sort}`;
    }

    // Construir URL completa y ejecutar GET
    const url = `${this.urlBase}/clients/search/page${query}`;
    return this.http.get<Page<ClientResponse>>(url);
  }

}
