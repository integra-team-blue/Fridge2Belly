import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Ingredient {
  id: string;
  name: string;
  quantity: number;
  unit: string;
  expirationDate: Date;
  calories: number;
  protein: number;
  fat: number;
  carbohydrates: number;
}

@Injectable({
  providedIn: 'root'
})

export class IngredientsService {
  private apiUrl = 'http://localhost:8080/api/ingredients';

  constructor(private http: HttpClient) {}

  getIngredients(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(this.apiUrl);
  }
}
