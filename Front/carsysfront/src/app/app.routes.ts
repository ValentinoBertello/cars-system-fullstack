import { Routes } from '@angular/router';
import { LandingComponent } from './landing/landing/landing.component';
import { CreateUserComponent } from './users/create-user/create-user.component';
import { LoginComponent } from './users/login/login.component';
import { CreateCarComponent } from './cars/create-car/create-car.component';
import { ListCarsComponent } from './cars/list-cars/list-cars.component';
import { ListSalesComponent } from './sales/list-sales/list-sales.component';
import { CreateSaleComponent } from './sales/create-sale/create-sale.component';
import { ListClientsComponent } from './customers/list-clients/list-clients.component';

export const routes: Routes = [
    
    // Users
    { path: '', redirectTo: '/landing', pathMatch: 'full' },
    { path: 'landing', component: LandingComponent },
    { path: 'user/create', component: CreateUserComponent },
    { path: 'user/login', component: LoginComponent },

    //cars
    { path: 'car/create', component: CreateCarComponent },
    { path: 'car/list', component: ListCarsComponent },

    //sales
    { path: 'sale/list', component: ListSalesComponent },
    { path: 'sale/create', component: CreateSaleComponent },

    //clientes
    { path: 'client/list', component: ListClientsComponent },
];
