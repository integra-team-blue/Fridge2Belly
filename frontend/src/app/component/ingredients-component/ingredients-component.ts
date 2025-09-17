import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import {Ingredient, IngredientsService} from '../../services/ingredients-services/ingredients-service';
import {DatePipe, DecimalPipe} from '@angular/common';
import {MenuItem} from 'primeng/api';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-ingredients-component',
  standalone: true,
  templateUrl: './ingredients-component.html',
  imports: [TableModule, DatePipe, DecimalPipe, RouterModule],
  styleUrls: ['./ingredients-component.css']
})

export class IngredientsComponent {
  items: MenuItem[] = [];
  ingredients: Ingredient[] = [];

  constructor(private ingredientsService: IngredientsService) {}

  ngOnInit() {
    this.ingredientsService.getIngredients().subscribe(data => {
      this.ingredients = data;
    });

    this.items = [
      { label: 'Meals', routerLink: ['/meals'] },
      { label: 'Dishes', routerLink: ['/dishes'] },
      { label: 'Recipes', routerLink: ['/recipes'] },
      { label: 'Ingredients', routerLink: ['/ingredients'] }
    ];
  }
}
