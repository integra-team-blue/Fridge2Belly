import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import {
  Ingredient,
  IngredientsService,
} from '../../services/ingredients-services/ingredients-service';
import { DatePipe, DecimalPipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { MessageService } from 'primeng/api';

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
  ],
  styleUrls: ['./ingredients-component.css'],
})
export class IngredientsComponent {
  ingredients: Ingredient[] = [];
  showDialog = false;
  selectedIngredient: Ingredient | null = null;
  editDialogVisible = false;

  constructor(
    private ingredientsService: IngredientsService,
    private messageService: MessageService,
  ) {}

  async ngOnInit() {
    this.ingredients = await firstValueFrom(this.ingredientsService.getIngredients());
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
      const ingredientToSave: Ingredient = {
        name: this.ingredientForm.value.name ?? '',
        unit: this.ingredientForm.value.unit ?? '',
        quantity: Number(this.ingredientForm.value.quantity ?? 0),
        expirationDate: this.ingredientForm.value.expirationDate ?? new Date(),
        calories: Number(this.ingredientForm.value.calories ?? 0),
        protein: Number(this.ingredientForm.value.protein ?? 0),
        fat: Number(this.ingredientForm.value.fat ?? 0),
        carbohydrates: Number(this.ingredientForm.value.carbohydrates ?? 0),
      };

      try {
        const savedIngredient = await firstValueFrom(
          this.ingredientsService.addIngredient(ingredientToSave),
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

  openEditDialog(ingredient: Ingredient[] | Ingredient | undefined) {
    if (ingredient == null || Array.isArray(ingredient)) {
      return;
    }

    this.selectedIngredient = ingredient;

    this.editForm.patchValue({
      name: ingredient.name,
      quantity: ingredient.quantity,
      unit: ingredient.unit,
      expirationDate:
        ingredient.expirationDate != null ? new Date(ingredient.expirationDate) : new Date(),
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
    expirationDate: new FormControl<Date | null>(null),
    calories: new FormControl(0, { nonNullable: true }),
    protein: new FormControl(0, { nonNullable: true }),
    fat: new FormControl(0, { nonNullable: true }),
    carbohydrates: new FormControl(0, { nonNullable: true }),
  });

  async editIngredient() {
    if (this.editForm.valid && this.selectedIngredient?.id != null) {
      const updatedIngredient: Ingredient = {
        ...this.selectedIngredient,
        name: this.editForm.value.name ?? '',
        quantity: Number(this.editForm.value.quantity ?? 0),
        unit: this.editForm.value.unit ?? '',
        expirationDate: this.editForm.value.expirationDate ?? new Date(),
        calories: Number(this.editForm.value.calories ?? 0),
        protein: Number(this.editForm.value.protein ?? 0),
        fat: Number(this.editForm.value.fat ?? 0),
        carbohydrates: Number(this.editForm.value.carbohydrates ?? 0),
      };

      try {
        const saved = await firstValueFrom(
          this.ingredientsService.updateIngredient(this.selectedIngredient.id, updatedIngredient),
        );

        const index = this.ingredients.findIndex((i) => i.id === this.selectedIngredient!.id);
        if (index !== -1) {
          this.ingredients[index] = saved;
        }

        this.editDialogVisible = false;
        this.selectedIngredient = null;
        this.ingredientForm.reset();
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
}
