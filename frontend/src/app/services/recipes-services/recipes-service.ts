import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {FormControl, ɵFormGroupRawValue, ɵTypedOrUntyped} from '@angular/forms';

export type Recipe = {
  id: string;
  name: string;
  description: string;
  cookingTimeMinutes: number;
  instructions: string;
  dishIds: string[];
};

@Injectable({ providedIn: 'root' })
export class RecipesService {
  private apiUrl = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) {}

  getRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.apiUrl);
  }

  addRecipe(recipe: {
    instructions: string | undefined;
    cookingTimeMinutes: number | undefined;
    name: string | undefined;
    description: string | undefined;
    dishIds: string[] | undefined
  }): Observable<Recipe> {
    return this.http.post<Recipe>(this.apiUrl, recipe);
  }

  updateRecipe(id: string, recipe: ɵTypedOrUntyped<{
    instructions: FormControl<string>;
    cookingTimeMinutes: FormControl<number>;
    name: FormControl<string>;
    description: FormControl<string>;
    dishIds: FormControl<string[]>
  }, ɵFormGroupRawValue<{
    instructions: FormControl<string>;
    cookingTimeMinutes: FormControl<number>;
    name: FormControl<string>;
    description: FormControl<string>;
    dishIds: FormControl<string[]>
  }>, any>): Observable<Recipe> {
    return this.http.put<Recipe>(`${this.apiUrl}/${id}`, recipe);
  }

  deleteRecipe(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
