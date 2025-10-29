import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom, map } from 'rxjs';

export type UserDto = {
  id?: string;
  username: string;
  email: string;
};

export type AuthResponse = {
  token: string;
  user: UserDto;
};

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private api = 'http://localhost:8080';

  async getUsers(): Promise<UserDto[]> {
    return await firstValueFrom(this.http.get<UserDto[]>(`${this.api}/users`));
  }

  private currentUser: UserDto | null = null; // 🔹 adaugăm user curent

  async login(body: { email: string; password: string }): Promise<AuthResponse> {
    const response = await firstValueFrom(
      this.http.post<AuthResponse>(`${this.api}/auth/login`, { email: body.email }),
    );

    localStorage.setItem('token', response.token);
    this.currentUser = response.user; // 🔹 salvăm userul logat

    return response;
  }

  getCurrentUser(): UserDto | null {
    return this.currentUser;
  }

  async signup(body: { username: string; email: string }): Promise<AuthResponse> {
    const user = await firstValueFrom(this.http.post<UserDto>(`${this.api}/users`, body));
    const response: AuthResponse = { token: 'temp-token', user };
    localStorage.setItem('token', response.token);
    return response;
  }

  isUsernameTaken(username: string) {
    return this.http
      .get<{ taken: boolean }>(`${this.api}/users/check-username`, { params: { username } })
      .pipe(map((r) => Boolean(r.taken)));
  }

  isEmailTaken(email: string) {
    return this.http
      .get<{ taken: boolean }>(`${this.api}/users/check-email`, { params: { email } })
      .pipe(map((r) => Boolean(r.taken)));
  }

  logout() {
    localStorage.removeItem('token');
  }

  isAuthenticated() {
    return localStorage.getItem('token') != null;
  }
}
