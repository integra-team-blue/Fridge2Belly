import { Component, OnInit } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Meal, MealsService } from '../../services/meals-services/meals-service';
import { TableModule } from 'primeng/table';
import { DatePipe, NgForOf } from '@angular/common';

@Component({
  selector: 'app-meals',
  templateUrl: './meals-component.html',
  styleUrls: ['./meals-component.css'],
  imports: [
    TableModule,
    DatePipe,
    NgForOf
  ]
})
export class MealsComponent implements OnInit {
  meals: Meal[] = [];

  constructor(private mealsService: MealsService) {}

  async ngOnInit() {
    try {
      this.meals = await firstValueFrom(this.mealsService.getMeals());
      console.log('Meals loaded:', this.meals);
    } catch (error) {
      console.error('Error loading meals:', error);
    }
  }
}
