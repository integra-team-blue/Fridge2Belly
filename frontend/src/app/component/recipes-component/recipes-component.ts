import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Dialog } from 'primeng/dialog';
import { Button } from 'primeng/button';
import { MultiSelect } from 'primeng/multiselect';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { ContextMenu } from 'primeng/contextmenu';
import { ConfirmDialog } from 'primeng/confirmdialog';

import {RecipeControllerService, RecipeDto, DishControllerService, DishDto } from '../../api'
import { InputText } from 'primeng/inputtext';
import { Textarea } from 'primeng/textarea';

@Component({
  selector: 'app-recipes-component',
  standalone: true,
  templateUrl: './recipes-component.html',
  imports: [
    TableModule,
    RouterModule,
    CommonModule,
    ConfirmDialog,
    ContextMenu,
    Button,
    Dialog,
    ReactiveFormsModule,
    MultiSelect,
    InputText,
    Textarea,
  ],
  styleUrls: ['./recipes-component.css'],
})
export class RecipesComponent {
  recipes: RecipeDto[] = [];
  dishes: DishDto[] = [];

  selectedRecipe: RecipeDto | null = null;
  showDialog = false;
  editDialogVisible = false;

  menuItems: MenuItem[] = [];

  constructor(
    private recipeService: RecipeControllerService,
    private dishService: DishControllerService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {
    this.menuItems = [
      {
        label: 'Delete',
        icon: 'pi pi-times',
        command: () => this.confirmDelete(this.selectedRecipe),
      },
    ];
  }

  async ngOnInit() {
    try {
      const recipesResponse = await firstValueFrom(
        this.recipeService.getAllRecipes('body', false, { httpHeaderAccept: 'application/json' as '*/*' })
      );

      this.recipes = (recipesResponse || []).map(r => ({
        ...r,
        dishIds: Array.isArray(r.dishIds) ? r.dishIds : [],
      }));

      console.log('Recipes loaded:', this.recipes);

      const dishesResponse = await firstValueFrom(
        this.dishService.getAll('body', false, { httpHeaderAccept: 'application/json' as '*/*' })
      );

      this.dishes = dishesResponse || [];
      console.log('Dishes loaded:', this.dishes);
    } catch (err) {
      console.error('Error loading data', err);
    }

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
      this.messageService.add({
        severity: 'warn',
        summary: 'No dishes',
        detail: 'Cannot add a recipe because no dishes are available.'
      });
      return;
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
    if (this.recipeForm.valid === false) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Invalid',
        detail: 'Please fill all required fields.',
      });
      return;
    }

    const formValue = this.recipeForm.getRawValue();

    const recipeToSave: RecipeDto = {
      name: formValue.name,
      description: formValue.description || undefined,
      cookingTimeMinutes: formValue.cookingTimeMinutes || undefined,
      instructions: formValue.instructions,
      dishIds: formValue.dishIds || [],
    };

    try {
      const saved = await firstValueFrom(this.recipeService.createRecipe(recipeToSave));
      const normalizedSaved = { ...saved, dishIds: Array.isArray(saved.dishIds) ? saved.dishIds : [] };
      this.recipes.push(normalizedSaved);
      this.showDialog = false;

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

  openEditDialog(recipe: RecipeDto[] | RecipeDto | undefined) {
    if (recipe == null || Array.isArray(recipe)) {
      return;
    }

    const r: RecipeDto = recipe;
    this.selectedRecipe = r;
    this.editForm.patchValue(r);
    this.editDialogVisible = true;
  }

  async editRecipe() {
    if (this.editForm.valid === false || this.selectedRecipe == null) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Invalid',
        detail: 'Please fill all required fields.',
      });
      return;
    }

    const updated = this.editForm.getRawValue();

    try {
      const formValue = this.editForm.getRawValue();
      const updatedRecipe: RecipeDto = {
        name: formValue.name,
        description: formValue.description || undefined,
        cookingTimeMinutes: formValue.cookingTimeMinutes || undefined,
        instructions: formValue.instructions,
        dishIds: formValue.dishIds || [],
      };
      const saved = await firstValueFrom(
        this.recipeService.updateRecipe(this.selectedRecipe.id!, updatedRecipe)
      );
      const idx = this.recipes.findIndex((r) => r.id === this.selectedRecipe!.id);
      if (idx !== -1) {
        this.recipes[idx] = saved;
      }

      this.editDialogVisible = false;
      this.selectedRecipe = null;
      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Recipe updated.',
      });
    } catch (error) {
      console.error(error);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Update failed.' });
    }
  }

  async deleteRecipe(recipe: RecipeDto | null) {
    if (recipe?.id == null) {
      return;
    }

    try {
      await firstValueFrom(this.recipeService.deleteRecipe(recipe.id));
      this.recipes = this.recipes.filter((r) => r.id !== recipe.id);
      this.selectedRecipe = null;

      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'The recipe was deleted successfully.',
      });
    } catch (err) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to delete the recipe. Please try again.',
      });
    }
  }

  confirmDelete(recipe: RecipeDto | null) {
    if (recipe == null) {
      return;
    }

    this.confirmationService.confirm({
      message: `Are you sure you want to delete "${recipe.name}"?`,
      accept: () => this.deleteRecipe(recipe),
      reject: () => {},
    });
  }

  getDishName(id: string): string {
    const dish = this.dishes.find(d => d.id === id);
    return dish ? dish.name : 'Unknown';
  }
}
