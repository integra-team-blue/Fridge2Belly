import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {UserDto} from '../auth-services/auth.service';

export type Ingredient = {
  id?: string;
  name: string;
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
export class IngredientsService {
  private apiUrl = 'http://localhost:8080/api/ingredients';

  constructor(private http: HttpClient) {}

  getIngredients(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(this.apiUrl);
  }

  addIngredient(ingredient: Ingredient): Observable<Ingredient> {
    return this.http.post<Ingredient>(this.apiUrl, ingredient);
  }

  updateIngredient(id: string, ingredient: Ingredient): Observable<Ingredient> {
    return this.http.put<Ingredient>(`${this.apiUrl}/${id}`, ingredient);
  }

  deleteIngredient(id: string): Observable<Ingredient> {
    return this.http.delete<Ingredient>(`${this.apiUrl}/${id}`);
  }

  addIngredientToUser(ingredientId: string, userId: string | undefined) {
    console.log(ingredientId, userId);
    return this.http.post<void>(
      `http://localhost:8080/api/user-ingredients/user/${userId}/ingredient/${ingredientId}`,
      {}
    );
  }

  getIngredientsForUser(userId: string| undefined) {
    return this.http.get<Ingredient[]>(`http://localhost:8080/api/user-ingredients/user/${userId}`);
  }

  removeIngredientFromUser(ingredientId: string , userId: string | undefined) {
    console.log(ingredientId, userId);
    return this.http.delete<void>(`http://localhost:8080/api/user-ingredients/user/${userId}/ingredient/${ingredientId}`);
  }
}
