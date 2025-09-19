import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { MealsService, Meal, Dish } from '../../services/meals-services/meals-service';
import {DatePipe, NgIf, NgFor, NgForOf} from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-meals-component',
  standalone: true,
  templateUrl: './meals-component.html',
  styleUrls: ['./meals-component.css'],
  imports: [
    TableModule, SelectModule, FormsModule,
    DatePipe, RouterModule
  ]
})
export class MealsComponent {
  meals: Meal[] = [];
  dishes: Dish[] = [];
  selectedDishIds: { [mealId: string]: string | null } = {};  editMode = false;
  constructor(private mealsService: MealsService) {}

  async ngOnInit() {
    this.meals = await firstValueFrom(this.mealsService.getMeals());
    this.meals.forEach(meal => {
      this.selectedDishIds[meal.id] = meal.dishIds.length > 0 ? meal.dishIds[0] : null;
    });
  }

  dishId = [
    { name : 'kg' , code : 'kg'}
  ];

  getDishesForMeal(meal: Meal): Dish[] {
    this.dishes.filter(d => meal.dishIds.includes(d.id));
    console.log(this.dishes);
    return this.dishes;
  }
}
