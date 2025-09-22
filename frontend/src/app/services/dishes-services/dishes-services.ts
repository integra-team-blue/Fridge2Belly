import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

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

@Injectable({ providedIn: 'root' })
export class DishesService {
  private apiUrl = 'http://localhost:8080/api/dishes';

  constructor(private http: HttpClient) {}

  getDishes(): Observable<Dish[]> {
    return this.http.get<Dish[]>(this.apiUrl);
  }
}
