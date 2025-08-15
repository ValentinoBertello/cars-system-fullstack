import { inject, Injectable } from '@angular/core';
import { GlobalService } from '../global.service';
import { HttpClient } from '@angular/common/http';
import { CarPost, CarResponse, UpdateCarDto } from '../../models/car';
import { Observable } from 'rxjs';
import { Page } from '../../models/page';

@Injectable({
  providedIn: 'root'
})
export class CarService {

  private readonly globalUrls = inject(GlobalService);
  private readonly urlBase = this.globalUrls.apiUrlLocalHost8085;
  private readonly http = inject(HttpClient);

  // Guardamos un nuevo auto
  saveCar(car: CarPost): Observable<CarResponse> {
    return this.http.post<CarResponse>(this.urlBase + "/cars/register", car);
  }

  // Traemos autos paginados con filtros opcionales
  getCarsPageByFilters(
    filters: { licensePlate?: string; brand?: string; model?: string },
    page: number,
    size: number = 10,
    sort?: string
  ): Observable<Page<CarResponse>> {
    // Comenzamos con los parámetros de paginación obligatorios
    let query = `?page=${page}&size=${size}`;

    // Añadimos filtro por patente de auto, si hay
    if (filters.licensePlate) {
      query += `&licensePlate=${filters.licensePlate}`;
    }

    // Filtro por marca si fue provisto
    if (filters.brand) {
      query += `&brand=${filters.brand}`;
    }

    // Filtro por modelo si fue provisto
    if (filters.model) {
      query += `&model=${filters.model}`;
    }

    if (sort) {
      query += `&sort=${sort}`;
    }

    // Construir URL completa y ejecutar GET
    const url = `${this.urlBase}/cars/search/page${query}`;
    return this.http.get<Page<CarResponse>>(url);
  }

  // Guardamos un nuevo auto
  updateCar(car: UpdateCarDto): Observable<CarResponse> {
    return this.http.put<CarResponse>(this.urlBase + "/cars/update", car);
  }

  // Traemos autos con filtro de marca, modelo o patente
  getCarsByFilters(carQuery: string): Observable<CarResponse[]> {
    return this.http.get<CarResponse[]>(this.urlBase + "/cars/search?carQuery=" + carQuery);
  }

}
