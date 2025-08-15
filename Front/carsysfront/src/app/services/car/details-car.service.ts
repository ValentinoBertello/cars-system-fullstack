import { inject, Injectable } from '@angular/core';
import { GlobalService } from '../global.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Brand, Model } from '../../models/car';

@Injectable({
  providedIn: 'root'
})
export class DetailsCarService {

  private readonly globalUrls = inject(GlobalService);
  private readonly urlBase = this.globalUrls.apiUrlLocalHost8085;
  private readonly http = inject(HttpClient);
  
   // Traemos todas las marcas
    getAllBrands(): Observable<Brand[]> {
      return this.http.get<Brand[]>(this.urlBase + "/cars/brands");
    }

    // Traemos todas los modelos
    getAllModels(): Observable<Model[]> {
      return this.http.get<Model[]>(this.urlBase + "/cars/models");
    }

    // Preguntamos si existe un auto con esa patente
    existsLicensePlate(licensePlate: string): Observable<boolean> {
      return this.http.get<boolean>(this.urlBase + "/cars/exists/license-plate/" + licensePlate);
    }
}
