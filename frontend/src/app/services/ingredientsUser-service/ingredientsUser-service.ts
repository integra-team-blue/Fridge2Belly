import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ingredient } from '../ingredients-services/ingredients-service';

export type UserIngredientUpdate = {
  quantity: number;
  unit: string;
  expirationDate: Date;
  calories: number;
  protein: number;
  fat: number;
  carbohydrates: number;
};

@Injectable({
  providedIn: 'root',
})
export class IngredientsUserService {
  private apiUrl = 'http://localhost:8080/api/user-ingredients';

  constructor(private http: HttpClient) {}

  addIngredientToUser(ingredientId: string, userId: string | undefined) {
    return this.http.post<void>(`${this.apiUrl}/user/${userId}/ingredient/${ingredientId}`, {});
  }

  getIngredientsForUser(userId: string | undefined) {
    return this.http.get<Ingredient[]>(`${this.apiUrl}/user/${userId}`);
  }

  removeIngredientFromUser(ingredientUserId: string) {
    return this.http.delete<void>(`${this.apiUrl}/${ingredientUserId}`);
  }

  updateIngredientFromUser(id: string, dto: UserIngredientUpdate): Observable<Ingredient> {
    return this.http.put<Ingredient>(`${this.apiUrl}/${id}`, dto);
  }
}
