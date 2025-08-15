import { HttpInterceptorFn } from "@angular/common/http";
import { AuthService } from "../services/users/auth.service";
import { inject } from "@angular/core";

// Con esta función No necesitamos pensar en agregar el token manualmente en cada petición.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);

    // const publicEndpoints = [
    //     '',
    // ];

    //Verifica si la solicitud es para un endpoint público
    // const idPublicRequest = publicEndpoints.some(endpoint =>
    //     req.url.includes(endpoint)
    // );

    // Si es pública, NO agregamos el token
    // if (isPublicRequest) {
    //     return next(req);
    // }

    // Para endpoints privados: agregamos el token
    const token = authService.getToken();
    if (token) {
        const clonedReq = req.clone({
            headers: req.headers.set('Authorization', `Bearer ${token}`)
        });
        return next(clonedReq);
    }

    return next(req);
}