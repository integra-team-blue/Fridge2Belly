import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Dish} from '../dishes-services/dishes-services';

export type Meal = {
  id: string;
  mealType: string;
  dateTime: string;
  dishIds: string[];
  dishes: Dish[];
};

@Injectable({ providedIn: 'root' })
export class MealsService {
  private apiUrl = 'http://localhost:8080/api/meals';

  constructor(private http: HttpClient) {}

  getMeals(): Observable<Meal[]> {
    return this.http.get<Meal[]>(this.apiUrl);
  }
}
