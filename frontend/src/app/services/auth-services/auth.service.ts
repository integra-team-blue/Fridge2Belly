import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

type AuthResponse = {
  token: string;
  user: any;
};
export type UserDto = {
  id?: string;
  username: string;
  email: string;
};

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private api = 'http://localhost:8080';

  login(body: { email: string; password: string }) {
    return new Promise<AuthResponse>((resolve) =>
      setTimeout(() => resolve({ token: 'temp-token', user: { email: body.email } }), 400),
    ) as any;
  }

  signup(body: { username: string; email: string }) {
    return this.http.post<UserDto>(`${this.api}/users`, body).pipe(
      map((user) => ({ token: 'temp-token', user }) as AuthResponse),
      tap((res) => localStorage.setItem('token', res.token)),
    );
  }

  getUsers(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.api}/users`);
  }

  isUsernameTaken(username: string): Observable<boolean> {
    return this.getUsers().pipe(
      map((list) => list.some((u) => u.username.toLowerCase() === username.toLowerCase())),
    );
  }

  isEmailTaken(email: string): Observable<boolean> {
    return this.getUsers().pipe(
      map((list) => list.some((u) => u.email.toLowerCase() === email.toLowerCase())),
    );
  }

  logout() {
    localStorage.removeItem('token');
  }
  isAuthenticated() {
    return Boolean(localStorage.getItem('token'));
  }
}
