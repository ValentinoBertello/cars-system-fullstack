export interface Brand {
  id: number;
  name: string;
}

export interface Model {
  id: number;
  name: string;
  brandName: string;
}

export interface CarPost {
  licensePlate: string;
  modelId: number;
  userEmail: string;
  year: number;
  color: string;
  basePrice: number;
  mileage: number;
}

export interface CarResponse {
  id: number;
  licensePlate: string;
  modelName: string;
  brandName: string;
  userId: number;
  userEmail: string;
  year: number;
  color: string;
  basePrice: number;
  mileage: number;
  status: string;
  registrationDate: string;
}

export interface UpdateCarDto {
  id: number;
  basePrice: number;
  mileage: number;
}

