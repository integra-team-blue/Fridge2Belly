import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import {Ingredient, IngredientsService} from '../../services/ingredients-services/ingredients-service';
import {DatePipe, DecimalPipe} from '@angular/common';
import { RouterModule } from '@angular/router';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-ingredients-component',
  standalone: true,
  templateUrl: './ingredients-component.html',
  imports: [TableModule, DatePipe, DecimalPipe, RouterModule],
  styleUrls: ['./ingredients-component.css']
})

export class IngredientsComponent {
  ingredients: Ingredient[] = [];

  constructor(private ingredientsService: IngredientsService) {}

  async ngOnInit() {
    this.ingredientsService.getIngredients().subscribe(data => {
      this.ingredients = data;
    });
    this.ingredients = await firstValueFrom(this.ingredientsService.getIngredients());
  }
}
