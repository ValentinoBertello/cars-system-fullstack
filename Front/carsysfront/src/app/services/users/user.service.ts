import { inject, Injectable } from '@angular/core';
import { GlobalService } from '../global.service';
import { HttpClient } from '@angular/common/http';
import { UserGet, UserPost } from '../../models/users';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly globalUrls = inject(GlobalService);
  private readonly urlBase = this.globalUrls.apiUrlLocalHost8085;
  private readonly http = inject(HttpClient);

  // Guardamos un nuevo usuario
  postUser(user: UserPost): Observable<UserGet> {
    return this.http.post<UserGet>(this.urlBase + "/users/register", user);
  }
  
  // Revisamos si el email ya exista en la bd
  checkEmailRepeated(email: string): Observable<boolean> {
    return this.http.get<boolean>(this.urlBase + "/users/check-email/" + email)
  }
}
