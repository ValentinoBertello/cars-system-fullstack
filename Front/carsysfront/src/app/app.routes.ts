import { Routes } from '@angular/router';
import { LandingComponent } from './landing/landing/landing.component';
import { CreateUserComponent } from './users/create-user/create-user.component';
import { LoginComponent } from './users/login/login.component';

export const routes: Routes = [
    // Users
    { path: '', redirectTo: '/landing-page', pathMatch: 'full' },
    { path: 'landing-page', component: LandingComponent },
    { path: 'create-user', component: CreateUserComponent },
    { path: 'login', component: LoginComponent }
];
