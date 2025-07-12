import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalService {

  get apiUrlLocalHost8085(): string {
    return "http://localhost:8085";
  }

 }
