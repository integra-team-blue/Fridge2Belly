import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Dish, DishRef } from '../dishes-services/dishes-services';

export type Meal = {
  id: string;
  mealType: string;
  dateTime: string;
  dishes: Dish[];
};

export type MealPayload = {
  mealType: string;
  dateTime: string;
  dishes: DishRef[];
};

@Injectable({ providedIn: 'root' })
export class MealsService {
  private apiUrl = 'http://localhost:8080/api/meals';

  constructor(private http: HttpClient) {}

  getMeals(): Observable<Meal[]> {
    return this.http.get<Meal[]>(this.apiUrl);
  }

  addMeal(meal: MealPayload): Observable<Meal> {
    return this.http.post<Meal>(this.apiUrl, meal);
  }

  updateMeal(id: string, meal: MealPayload): Observable<Meal> {
    return this.http.put<Meal>(`${this.apiUrl}/${id}`, meal);
  }

  deleteMeal(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
