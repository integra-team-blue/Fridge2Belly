import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type Dish = {
  id: string;
  name: string;
  preparedAt: string;
  calories: number;
  protein: number;
  fat: number;
  carbohydrates: number;
  recipeIds: string[];
  ingredientIds: string[];
};

export type RecipeOption = { id: string; name: string };

export type CreateDishPayload = {
  name: string;
  preparedAt: string;
  calories: number;
  protein: number;
  fat: number;
  carbohydrates: number;
  recipeIds: string[];
  ingredientIds: string[];
};

@Injectable({ providedIn: 'root' })
export class DishesService {
  private dishesUrl = 'http://localhost:8080/api/dishes';
  private recipesUrl = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) {}

  getDishes(): Observable<Dish[]> {
    return this.http.get<Dish[]>(this.dishesUrl);
  }

  getRecipes(): Observable<RecipeOption[]> {
    return this.http.get<RecipeOption[]>(this.recipesUrl);
  }

  createDish(body: CreateDishPayload): Observable<Dish> {
    return this.http.post<Dish>(this.dishesUrl, body);
  }

  updateDish(id: string, body: CreateDishPayload): Observable<Dish> {
    return this.http.put<Dish>(`${this.dishesUrl}/${id}`, body);
  }

  deleteDish(id: string): Observable<void> {
    return this.http.delete<void>(`${this.dishesUrl}/${id}`);
  }
}
