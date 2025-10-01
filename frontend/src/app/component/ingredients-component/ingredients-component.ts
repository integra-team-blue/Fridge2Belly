import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';

import {IngredientsControllerService, IngredientDto } from '../../api'
import { DatePipe, DecimalPipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { ContextMenu } from 'primeng/contextmenu';
import { ConfirmDialog } from 'primeng/confirmdialog';

@Component({
  selector: 'app-ingredients-component',
  standalone: true,
  templateUrl: './ingredients-component.html',
  imports: [
    TableModule,
    DatePipe,
    DecimalPipe,
    RouterModule,
    ButtonModule,
    DialogModule,
    ReactiveFormsModule,
    InputTextModule,
    SelectModule,
    FormsModule,
    ContextMenu,
    ConfirmDialog,
  ],
  styleUrls: ['./ingredients-component.css'],
})
export class IngredientsComponent {
  ingredients: IngredientDto[] = [];
  showDialog = false;
  selectedIngredient: IngredientDto | null = null;
  editDialogVisible = false;
  menuItems: MenuItem[];

  constructor(
    private ingredientsService: IngredientsControllerService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {
    this.menuItems = [
      {
        label: 'Delete',
        icon: 'pi pi-times',
        command: () => this.confirmDelete(this.selectedIngredient),
      },
    ];
  }

  async ngOnInit() {
    this.ingredients = await firstValueFrom(
      this.ingredientsService.getAllIngredients('body', false, { httpHeaderAccept: 'application/json' as '*/*' })
    );
  }

  ingredientForm = new FormGroup({
    name: new FormControl('', { nonNullable: true }),
    quantity: new FormControl(0, { nonNullable: true }),
    unit: new FormControl('', { nonNullable: true }),
    expirationDate: new FormControl<Date>(new Date(), { nonNullable: true }),
    calories: new FormControl(0, { nonNullable: true }),
    protein: new FormControl(0, { nonNullable: true }),
    fat: new FormControl(0, { nonNullable: true }),
    carbohydrates: new FormControl(0, { nonNullable: true }),
  });

  unitOptions = [
    { name: 'kg', code: 'kg' },
    { name: 'g', code: 'g' },
    { name: 'l', code: 'l' },
    { name: 'ml', code: 'ml' },
    { name: 'pcs', code: 'pcs' },
  ];

  async addIngredient() {
    if (this.ingredientForm.valid) {
      const ingredientToSave: IngredientDto = {
        name: this.ingredientForm.value.name ?? '',
        quantity: Number(this.ingredientForm.value.quantity ?? 0),
        unit: this.ingredientForm.value.unit ?? '',
        expirationDate: this.ingredientForm.value.expirationDate
          ? new Date(this.ingredientForm.value.expirationDate).toISOString()
          : new Date().toISOString(),
        calories: Number(this.ingredientForm.value.calories ?? 0),
        protein: Number(this.ingredientForm.value.protein ?? 0),
        fat: Number(this.ingredientForm.value.fat ?? 0),
        carbohydrates: Number(this.ingredientForm.value.carbohydrates ?? 0),
      };

      try {
        const savedIngredient = await firstValueFrom(
          this.ingredientsService.addIngredient(ingredientToSave, 'body', false, { httpHeaderAccept: 'application/json' as '*/*' })
        );
        this.ingredients.push(savedIngredient);
        this.showDialog = false;
        this.ingredientForm.reset();
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'The ingredient was added successfully.',
        });
      } catch (err) {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to add the ingredient. Please try again.',
        });
      }
    }
  }

  openEditDialog(ingredient: IngredientDto[] | IngredientDto | undefined) {
    if (ingredient == null || Array.isArray(ingredient)) {
      return;
    }

    this.selectedIngredient = ingredient;

    this.editForm.patchValue({
      name: ingredient.name,
      quantity: ingredient.quantity,
      unit: ingredient.unit,
      expirationDate: ingredient.expirationDate
        ? new Date(ingredient.expirationDate).toISOString().split('T')[0]
        : null,
      calories: ingredient.calories,
      protein: ingredient.protein,
      fat: ingredient.fat,
      carbohydrates: ingredient.carbohydrates,
    });

    this.editDialogVisible = true;
  }

  editForm = new FormGroup({
    name: new FormControl('', { nonNullable: true }),
    quantity: new FormControl(0, { nonNullable: true }),
    unit: new FormControl('', { nonNullable: true }),
    expirationDate: new FormControl<string | null>(null),
    calories: new FormControl(0, { nonNullable: true }),
    protein: new FormControl(0, { nonNullable: true }),
    fat: new FormControl(0, { nonNullable: true }),
    carbohydrates: new FormControl(0, { nonNullable: true }),
  });

  async editIngredient() {
    if (this.editForm.valid && this.selectedIngredient?.id != null) {
      const updatedIngredient: IngredientDto = {
        ...this.selectedIngredient,
        name: this.editForm.value.name ?? '',
        quantity: Number(this.editForm.value.quantity ?? 0),
        unit: this.editForm.value.unit ?? '',
        expirationDate: this.editForm.value.expirationDate
          ? new Date(this.editForm.value.expirationDate).toISOString()
          : new Date().toISOString(),
        calories: Number(this.editForm.value.calories ?? 0),
        protein: Number(this.editForm.value.protein ?? 0),
        fat: Number(this.editForm.value.fat ?? 0),
        carbohydrates: Number(this.editForm.value.carbohydrates ?? 0),
      };

      try {
        const saved = await firstValueFrom(
          this.ingredientsService.updateIngredient(
            this.selectedIngredient.id!,
            updatedIngredient,
            'body',
            false,
            { httpHeaderAccept: 'application/json' as '*/*' }
          )
        );

        const index = this.ingredients.findIndex((i) => i.id === this.selectedIngredient!.id);
        if (index !== -1) {
          this.ingredients[index] = saved;
        }

        this.editDialogVisible = false;
        this.selectedIngredient = null;
        this.editForm.reset();
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'The ingredient was edited successfully.',
        });
      } catch (err) {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to edit the ingredient. Please try again.',
        });
      }
    }
  }

  async deleteIngredient(ingredient: IngredientDto | null) {
    if (ingredient?.id == null) {
      return;
    }

    try {
      await firstValueFrom(this.ingredientsService.deleteIngredient(ingredient.id));

      this.ingredients = this.ingredients.filter((i) => i.id !== ingredient.id);

      this.selectedIngredient = null;
      this.ingredientForm.reset();

      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'The ingredient was deleted successfully.',
      });
    } catch (err) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Failed to delete the ingredient. Please try again.',
      });
    }
  }

  confirmDelete(ingredient: IngredientDto | null) {
    if (ingredient == null) {
      return;
    }

    this.confirmationService.confirm({
      message: `Are you sure you want to delete "${ingredient.name}"?`,
      accept: () => this.deleteIngredient(ingredient),
      reject: () => {},
    });
  }
}
