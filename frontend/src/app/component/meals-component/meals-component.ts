import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Dialog } from 'primeng/dialog';
import { Button } from 'primeng/button';
import { MessageService } from 'primeng/api';

import { Meal, MealsService } from '../../services/meals-services/meals-service';
import { Dish, DishesService } from '../../services/dishes-services/dishes-services';
import { DatePicker } from 'primeng/datepicker';
import { Select } from 'primeng/select';

@Component({
  selector: 'app-meals',
  standalone: true,
  templateUrl: './meals-component.html',
  styleUrls: ['./meals-component.css'],
  imports: [
    TableModule,
    CommonModule,
    Dialog,
    Button,
    ReactiveFormsModule,
    DatePicker,
    Select,
  ],
})

export class MealsComponent {
  meals: Meal[] = [];
  dishes: Dish[] = [];

  mealTypes = [
    { label: 'Breakfast', value: 'BREAKFAST' },
    { label: 'Lunch', value: 'LUNCH' },
    { label: 'Dinner', value: 'DINNER' },
    { label: 'Snack', value: 'SNACK' },
  ];

  selectedMeal: Meal | null = null;
  showDialog = false;
  editDialogVisible = false;

  constructor(
    private mealsService: MealsService,
    private dishesService: DishesService,
    private messageService: MessageService,
  ) {}

  async ngOnInit() {
    try {
      this.meals = await firstValueFrom(this.mealsService.getMeals());
      this.dishes = await firstValueFrom(this.dishesService.getDishes());
    } catch (error) {
      console.error('Error loading meals or dishes:', error);
    }
  }

  mealForm = new FormGroup({
    mealType: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dateTime: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dishIds: new FormControl<string[]>([], {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  editForm = new FormGroup({
    mealType: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dateTime: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dishIds: new FormControl<string[]>([], {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  openAddDialog() {
    this.mealForm.reset({
      mealType: '',
      dateTime: '',
      dishIds: [],
    });
    this.showDialog = true;
  }

  async addMeal() {
    if (this.mealForm.invalid) {
      this.messageService.add({ severity: 'warn', summary: 'Invalid', detail: 'Fill all fields' });
      return;
    }

    const raw = this.mealForm.getRawValue();
    console.log('Payload final trimis:', raw);

    const payload = {
      mealType: (raw.mealType as string).toUpperCase(),
      dateTime: new Date(raw.dateTime!).toISOString().slice(0, 19),
      dishIds: Array.isArray(raw.dishIds)
        ? raw.dishIds.filter((id) => !! id)
        : [],
      dishes: [],
    };

    console.log('Payload final:', payload);

    try {
      const newMeal = await firstValueFrom(this.mealsService.addMeal(payload));
      this.meals.push(newMeal);
      this.showDialog = false;
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Meal added.' });
    } catch (err) {
      console.error('Eroare backend:', err);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Add failed.' });
    }
  }

  openEditDialog(meal: Meal | Meal[] | undefined) {
    if (! meal || Array.isArray(meal)) {
      return;
    }

    this.selectedMeal = meal;
    this.editForm.patchValue(meal);
    this.editDialogVisible = true;
  }

  async editMeal() {
    if (! this.selectedMeal) return;

    try {
      const updated = await firstValueFrom(
        this.mealsService.updateMeal(this.selectedMeal.id, this.editForm.getRawValue()),
      );
      const idx = this.meals.findIndex((m) => m.id === this.selectedMeal!.id);
      if (idx !== -1) {
        this.meals[idx] = updated;
      }
      this.editDialogVisible = false;
      this.selectedMeal = null;
      this.messageService.add({ severity: 'success', summary: 'Updated', detail: 'Meal updated.' });
    } catch (err) {
      console.error(err);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Update failed.' });
    }
  }
}
