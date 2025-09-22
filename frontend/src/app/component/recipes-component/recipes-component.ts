import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import {Dialog, DialogModule} from 'primeng/dialog';
import {Button, ButtonModule} from 'primeng/button';
//import { InputTextModule } from 'primeng/inputtext';
//import { InputTextareaModule } from 'primeng/inputtextarea/inputtextarea';
import {MultiSelect, MultiSelectModule} from 'primeng/multiselect';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { ContextMenu } from 'primeng/contextmenu';
import { ConfirmDialog } from 'primeng/confirmdialog';

import { Recipe, RecipesService } from '../../services/recipes-services/recipes-service';
import { Dish, DishesService } from '../../services/dishes-services/dishes-service';
import {InputText} from 'primeng/inputtext';
import {Textarea} from 'primeng/textarea';

@Component({
  selector: 'app-recipes-component',
  standalone: true,
  templateUrl: './recipes-component.html',
  imports: [TableModule, RouterModule, CommonModule, ConfirmDialog, ContextMenu, Button, Dialog, ReactiveFormsModule, MultiSelect, InputText, Textarea],
  styleUrls: ['./recipes-component.css'],
})
export class RecipesComponent {
  recipes: Recipe[] = [];
  dishes: Dish[] = [];

  selectedRecipe: Recipe | null = null;
  showDialog = false;
  editDialogVisible = false;

  menuItems: MenuItem[];

  constructor(
    private recipesService: RecipesService,
    private dishesService: DishesService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {
    this.menuItems = [
      {
        label: 'Delete',
        icon: 'pi pi-trash',
        command: () => this.confirmDelete(this.selectedRecipe),
      },
    ];
  }

  async ngOnInit() {
    this.recipes = await firstValueFrom(this.recipesService.getRecipes());
    this.dishes = await firstValueFrom(this.dishesService.getDishes());
  }

  recipeForm = new FormGroup({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    description: new FormControl('', { nonNullable: true }),
    cookingTimeMinutes: new FormControl(1, { nonNullable: true, validators: [Validators.min(1)] }),
    instructions: new FormControl('', { nonNullable: true }),
    dishIds: new FormControl<string[]>([], { nonNullable: true }),
  });

  editForm = new FormGroup({
    name: new FormControl('', { nonNullable: true }),
    description: new FormControl('', { nonNullable: true }),
    cookingTimeMinutes: new FormControl(1, { nonNullable: true, validators: [Validators.min(1)] }),
    instructions: new FormControl('', { nonNullable: true }),
    dishIds: new FormControl<string[]>([], { nonNullable: true }),
  });

  async openAddDialog() {
    if (this.dishes.length === 0) {
      this.dishes = await firstValueFrom(this.dishesService.getDishes());
    }
    this.recipeForm.reset({
      name: '',
      description: '',
      cookingTimeMinutes: 1,
      instructions: '',
      dishIds: [],
    });
    this.showDialog = true;
  }

  async addRecipe() {
    if (!this.recipeForm.valid) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Invalid',
        detail: 'Please fill all required fields.',
      });
      return;
    }

    // Construim obiectul pentru backend
    const recipeToSave = {
      name: this.recipeForm.value.name,
      description: this.recipeForm.value.description,
      cookingTimeMinutes: this.recipeForm.value.cookingTimeMinutes,
      instructions: this.recipeForm.value.instructions,
      dishIds: this.recipeForm.value.dishIds,
    };

    console.log('Adding recipe:', recipeToSave);

    try {
      const saved = await firstValueFrom(this.recipesService.addRecipe(recipeToSave));
      this.recipes.push(saved);
      this.showDialog = false;

      // Reset formular
      this.recipeForm.reset({
        name: '',
        description: '',
        cookingTimeMinutes: 1,
        instructions: '',
        dishIds: [],
      });

      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Recipe added.',
      });
    } catch (error) {
      console.error(error);
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Add failed.',
      });
    }
  }


  openEditDialog(recipe: Recipe[] | Recipe | undefined) {
    if (!recipe || Array.isArray(recipe)) return;

    const r: Recipe = recipe;
    this.selectedRecipe = r;
    this.editForm.patchValue(r);
    this.editDialogVisible = true;
  }

  async editRecipe() {
    if (!this.editForm.valid || !this.selectedRecipe) {
      this.messageService.add({ severity: 'warn', summary: 'Invalid', detail: 'Please fill all required fields.' });
      return;
    }

    const updated = this.editForm.getRawValue();
    console.log('Updating recipe:', updated);

    try {
      const saved = await firstValueFrom(this.recipesService.updateRecipe(this.selectedRecipe.id, updated));
      const idx = this.recipes.findIndex((r) => r.id === this.selectedRecipe!.id);
      if (idx !== -1) this.recipes[idx] = saved;

      this.editDialogVisible = false;
      this.selectedRecipe = null;
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Recipe updated.' });
    } catch (error) {
      console.error(error);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Update failed.' });
    }
  }

  async deleteRecipe(recipe: Recipe | null) {
    if (!recipe) return;

    try {
      await firstValueFrom(this.recipesService.deleteRecipe(recipe.id));
      this.recipes = this.recipes.filter((r) => r.id !== recipe.id);
      this.selectedRecipe = null;
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Recipe deleted.' });
    } catch {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Delete failed.' });
    }
  }

  confirmDelete(recipe: Recipe | null) {
    if (!recipe) return;

    this.confirmationService.confirm({
      message: `Delete recipe "${recipe.name}"?`,
      accept: () => this.deleteRecipe(recipe),
    });
  }
}
