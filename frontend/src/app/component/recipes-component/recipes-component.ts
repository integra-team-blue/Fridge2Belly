import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { CommonModule } from '@angular/common';
import { Recipe, RecipesService } from '../../services/recipes-services/recipes-service';

@Component({
  selector: 'app-recipes-component',
  standalone: true,
  templateUrl: './recipes-component.html',
  imports: [TableModule, RouterModule, CommonModule],
  styleUrls: ['./recipes-component.css'],
})
export class RecipesComponent {
  recipes: Recipe[] = [];

  constructor(private recipesService: RecipesService) {}

  async ngOnInit() {
    this.recipes = await firstValueFrom(this.recipesService.getRecipes());
  }
}
