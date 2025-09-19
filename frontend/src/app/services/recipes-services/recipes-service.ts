import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export type Recipe = {
  id: string;
  name: string;
  description: string;
  cookingTimeMinutes: number;
  instructions: string;
  ingredientsId: string[];
};

@Injectable({ providedIn: 'root' })
export class RecipesService {
  private apiUrl = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) {}

  getRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.apiUrl);
  }
}
