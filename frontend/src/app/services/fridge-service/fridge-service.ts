import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

export interface FridgeIngredient {
  id?: number;
  ingredientId: number;
  ingredientName: string;
  quantity: number;
  unit: string;
  expiryDate: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
}

@Injectable({
  providedIn: 'root'
})
export class FridgeService {

}
