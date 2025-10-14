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

import { MealControllerService, DishControllerService, MealDto, DishDto } from '../../api';

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
})
export class MealsComponent {
  meals: MealDto[] = [];
  dishes: DishDto[] = [];

  mealTypes = [
    { label: 'Breakfast', value: 'BREAKFAST' },
    { label: 'Lunch', value: 'LUNCH' },
    { label: 'Dinner', value: 'DINNER' },
    { label: 'Snack', value: 'SNACK' },
  ];

  selectedMeal: MealDto | null = null;
  showDialog = false;
  editDialogVisible = false;

  menuItems: MenuItem[] = [];

  constructor(
    private mealsService: MealControllerService,
    private dishesService: DishControllerService,
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
      this.meals = await firstValueFrom(
        this.mealsService.getAllMeals('body', false, {
          httpHeaderAccept: 'application/json' as '*/*',
        }),
      );

      this.dishes = await firstValueFrom(
        this.dishesService.getAll('body', false, { httpHeaderAccept: 'application/json' as '*/*' }),
      );
    } catch (error) {
      console.error('Error loading meals or dishes:', error);
    }
  }

  mealForm = new FormGroup({
    mealType: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
    dateTime: new FormControl<Date | string | null>(null, { validators: [Validators.required] }),
    dishes: new FormControl<string[]>([], {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  editForm = new FormGroup({
    mealType: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dateTime: new FormControl<Date | string | null>(null, { validators: [Validators.required] }),
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

  private buildPayload(form: FormGroup): MealDto {
    const raw = form.getRawValue();

    const dateTime = raw.dateTime instanceof Date ? raw.dateTime.toISOString() : raw.dateTime;

    const dishIds: string[] = Array.isArray(raw.dishes)
      ? raw.dishes
      : raw.dishes != null && raw.dishes !== ''
        ? [raw.dishes]
        : [];

    if (dishIds.length === 0) {
      console.warn('No dishes selected! Backend might reject this.');
    }

    return {
      id: undefined,
      mealType: (raw.mealType as string).toUpperCase() as MealDto.MealTypeEnum,
      dateTime,
      dishIds,
    };
  }

  private buildUpdatePayload(form: FormGroup, mealId: string): MealDto {
    const raw = form.getRawValue();
    const dateTime = raw.dateTime instanceof Date ? raw.dateTime.toISOString() : raw.dateTime;
    const dishIds: string[] = Array.isArray(raw.dishes)
      ? raw.dishes
      : raw.dishes != null && raw.dishes !== ''
        ? [raw.dishes]
        : [];

    return {
      id: mealId,
      mealType: (raw.mealType as string).toUpperCase() as MealDto.MealTypeEnum,
      dateTime,
      dishIds,
    };
  }

  async addMeal() {
    if (this.mealForm.invalid) {
      this.messageService.add({ severity: 'warn', summary: 'Invalid', detail: 'Fill all fields' });
      return;
    }

    const payload = this.buildPayload(this.mealForm);

    try {
      await firstValueFrom(this.mealsService.createMeal(payload));
      this.showDialog = false;
      this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Meal added.' });
    } catch (err) {
      console.error('Backend error:', err);
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Add failed.' });
    }
  }

  openEditDialog(meal: MealDto | MealDto[] | undefined) {
    if (meal == null || Array.isArray(meal)) {
      return;
    }

    this.selectedMeal = meal;

    const selectedDishIds = meal.dishIds ?? [];

    this.editForm.patchValue({
      mealType: meal.mealType,
      dateTime: new Date(meal.dateTime),
      dishes: selectedDishIds,
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
      await firstValueFrom(this.mealsService.updateMeal(this.selectedMeal.id!, payload));
      this.editDialogVisible = false;
      this.selectedMeal = null;
      await this.loadData();
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
        if (this.selectedMeal?.id != null && this.selectedMeal.id !== '') {
          this.deleteMeal(this.selectedMeal.id);
        } else {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Cannot delete meal: no ID',
          });
        }
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
