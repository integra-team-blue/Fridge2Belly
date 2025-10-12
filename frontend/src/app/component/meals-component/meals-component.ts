import { Component } from '@angular/core';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Dialog } from 'primeng/dialog';
import { Button } from 'primeng/button';
import { MessageService, MenuItem } from 'primeng/api';
import { ContextMenu } from 'primeng/contextmenu';
import { DatePicker } from 'primeng/datepicker';
import { Select } from 'primeng/select';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { MultiSelect } from 'primeng/multiselect';

import { Meal, MealsService, MealPayload } from '../../services/meals-services/meals-service';
import { Dish, DishesService, DishRef } from '../../services/dishes-services/dishes-services';

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
    ContextMenu,
    ConfirmDialog,
    MultiSelect,
  ],

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

  menuItems: MenuItem[] = [];

  constructor(
    private mealsService: MealsService,
    private dishesService: DishesService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {}

  async ngOnInit() {
    await this.loadData();

    this.menuItems = [
      {
        label: 'Delete Meal',
        icon: 'pi pi-trash',
        command: () => this.confirmDelete(),
      },
    ];
  }

  async loadData() {
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
    dishes: new FormControl<string[]>([], {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  editForm = new FormGroup({
    mealType: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dateTime: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dishes: new FormControl<string[]>([], {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  openAddDialog() {
    this.mealForm.reset({
      mealType: '',
      dateTime: '',
      dishes: [],
    });
    this.showDialog = true;
  }

  private buildPayload(form: FormGroup): MealPayload {
    const raw = form.getRawValue();
    const dateTime = new Date(raw.dateTime!).toISOString().slice(0, 19);

    const rawDishes = raw.dishes;
    const dishesArray = Array.isArray(rawDishes)
      ? rawDishes
      : Boolean(rawDishes)
        ? [rawDishes]
        : [];

    const dishesPayload: DishRef[] = dishesArray.map((d: any): DishRef => {
      if (typeof d === 'object' && d !== null) {
        return { id: d.id ?? undefined, name: d.name };
      }
      const found = this.dishes.find((dish) => dish.name === d);
      return found ? { id: found.id, name: found.name } : { name: d };
    });

    return {
      mealType: (raw.mealType as string).toUpperCase(),
      dateTime,
      dishes: dishesPayload,
    };
  }

  async addMeal() {
    if (this.mealForm.invalid) {
      this.messageService.add({ severity: 'warn', summary: 'Invalid', detail: 'Fill all fields' });
      return;
    }

    const payload = this.buildPayload(this.mealForm);

    try {
      const newMeal = await firstValueFrom(this.mealsService.addMeal(payload));
      this.meals.push(newMeal);
      this.showDialog = false;
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Meal added.' });
    } catch (err) {
      console.error('Backend error:', err);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Add failed.' });
    }
  }

  openEditDialog(meal: Meal | Meal[] | undefined) {
    if (meal == null || Array.isArray(meal)) {
      return;
    }

    this.selectedMeal = meal;
    const dishNames = meal.dishes?.map((d) => d.name) ?? [];

    this.editForm.patchValue({
      mealType: meal.mealType,
      dateTime: meal.dateTime,
      dishes: dishNames,
    });
    this.editDialogVisible = true;
  }

  async editMeal() {
    if (this.selectedMeal == null) {
      return;
    }

    if (this.editForm.invalid) {
      this.messageService.add({ severity: 'warn', summary: 'Invalid', detail: 'Fill all fields' });
      return;
    }

    const payload = this.buildPayload(this.editForm);

    try {
      const updated = await firstValueFrom(
        this.mealsService.updateMeal(this.selectedMeal.id, payload),
      );

      const idx = this.meals.findIndex((m) => m.id === this.selectedMeal!.id);
      if (idx !== -1) {
        this.meals[idx] = updated;
      }

      this.editDialogVisible = false;
      this.selectedMeal = null;
      this.messageService.add({
        severity: 'success',
        summary: 'Updated',
        detail: 'Meal updated successfully.',
      });
    } catch (err) {
      console.error('Backend error on update:', err);
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Meal update failed.',
      });
    }
  }

  confirmDelete() {
    if (this.selectedMeal == null) {
      return;
    }

    this.confirmationService.confirm({
      message: `Are you sure you want to delete the meal "${this.selectedMeal.mealType}"?`,
      header: 'Confirm Deletion',
      icon: 'pi pi-exclamation-triangle',
      acceptButtonStyleClass: 'p-button-danger',
      acceptIcon: 'pi pi-check',
      rejectIcon: 'pi pi-times',
      accept: () => {
        this.deleteMeal(this.selectedMeal!.id);
      },
      reject: () => {
        this.messageService.add({
          severity: 'info',
          summary: 'Cancelled',
          detail: 'Delete cancelled.',
        });
      },
    });
  }

  async deleteMeal(id: string) {
    try {
      await firstValueFrom(this.mealsService.deleteMeal(id));
      this.meals = this.meals.filter((m) => m.id !== id);
      this.messageService.add({ severity: 'success', summary: 'Deleted', detail: 'Meal deleted.' });
    } catch (err) {
      console.error('Error deleting meal:', err);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Delete failed.' });
    }
  }
}
