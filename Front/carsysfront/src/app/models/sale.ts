import { CarResponse } from "./car";
import { ClientResponse } from "./client";

export interface PostSaleDto {
    carId: number;
    salePrice: number;
    clientId: number;
}

export interface PostSaleWithClientDto {
    carId: number;
    salePrice: number;

    clientName: string;
    clientLastName: string;
    clientPhone: string;
    clientDni: string;
}

export interface SaleResponse {
    id: number;
    car: CarResponse;
    client: ClientResponse;
    saleDate: string;
    salePrice: number;
}