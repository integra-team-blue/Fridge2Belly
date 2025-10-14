import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { NgForOf, NgIf, DatePipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { DatePicker } from 'primeng/datepicker';
import { InputNumber } from 'primeng/inputnumber';
import { DishControllerService, DishDto, RecipeDto, RecipeControllerService } from '../../api';
import { LoadingService } from '../../services/loading.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-dishes',
  templateUrl: './dishes-component.html',
  styleUrls: ['./dishes-component.css'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf,
    NgForOf,
    DatePipe,
    TableModule,
    DialogModule,
    ConfirmDialogModule,
    Button,
    InputText,
    Select,
    DatePicker,
    InputNumber,
  ],
})
export class DishesComponent implements OnInit {
  dishes: DishDto[] = [];
  recipes: RecipeDto[] = [];
  dialogVisible = false;

  private fb = inject(FormBuilder);
  private dishesService = inject(DishControllerService);
  private recipesService = inject(RecipeControllerService);
  public loadingService = inject(LoadingService);
  private toastService = inject(ToastService);
  private confirmService = inject(ConfirmationService);
  private _editId = signal<string | null>(null);

  form: FormGroup = this.fb.group({
    name: ['', Validators.required],
    recipeId: [''],
    preparedAt: ['', Validators.required],
    calories: [0, Validators.required],
    protein: [0, Validators.required],
    fat: [0, Validators.required],
    carbohydrates: [0, Validators.required],
  });

  get name() {
    return this.form.get('name')!;
  }
  get recipeId() {
    return this.form.get('recipeId')!;
  }
  get preparedAt() {
    return this.form.get('preparedAt')!;
  }
  get calories() {
    return this.form.get('calories')!;
  }
  get protein() {
    return this.form.get('protein')!;
  }
  get fat() {
    return this.form.get('fat')!;
  }
  get carbohydrates() {
    return this.form.get('carbohydrates')!;
  }
  editId = () => this._editId();

  async ngOnInit() {
    await this.loadData();
  }

  async loadData() {
    this.loadingService.show();
    try {
      const dishes = await firstValueFrom(
        this.dishesService.getAll('body', false, { httpHeaderAccept: 'application/json' as '*/*' }),
      );
      const recipes = await firstValueFrom(
        this.recipesService.getAllRecipes('body', false, {
          httpHeaderAccept: 'application/json' as '*/*',
        }),
      );

      this.dishes = dishes;
      this.recipes = recipes;
    } finally {
      this.loadingService.hide();
    }
  }

  openCreate() {
    this._editId.set(null);
    this.form.reset({
      name: '',
      recipeId: '',
      preparedAt: '',
      calories: 0,
      protein: 0,
      fat: 0,
      carbohydrates: 0,
    });
    this.dialogVisible = true;
  }

  onRowSelect(event: { data?: DishDto | DishDto[] }) {
    const dish = Array.isArray(event?.data) ? event.data[0] : event?.data;
    if (dish == null) {
      return;
    }
    this._editId.set(dish.id ?? null);
    this.form.patchValue({
      name: dish.name,
      recipeId: dish.recipeIds?.[0] ?? '',
      preparedAt: new Date(dish.preparedAt),
      calories: dish.calories,
      protein: dish.protein,
      fat: dish.fat,
      carbohydrates: dish.carbohydrates,
    });
    this.dialogVisible = true;
  }

  onRightClick(event: MouseEvent, dish: DishDto) {
    event.preventDefault();
    this.confirmService.confirm({
      header: 'Delete dish',
      message: `Are you sure you want to delete "${dish.name}"?`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        if (dish.id !== null && dish.id !== undefined) {
          this.deleteDish(dish.id);
        } else {
          this.toastService.push('Cannot delete: dish has no ID', 'error');
        }
      },
    });
  }

  private async deleteDish(id: string) {
    this.loadingService.show();
    try {
      await firstValueFrom(this.dishesService._delete(id));
      this.dishes = this.dishes.filter((d) => d.id !== id);
      this.toastService.push('Dish deleted', 'success');
    } finally {
      this.loadingService.hide();
    }
  }

  async submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loadingService.show();
    try {
      const v = this.form.getRawValue();

      const recipeIds: string[] = v.recipeId != null && v.recipeId !== '' ? [v.recipeId] : [];

      let ingredientIds: string[] = [];
      const id = this._editId();
      if (id != null) {
        const existingDish = this.dishes.find((d) => d.id === id);
        if (existingDish?.ingredientIds && existingDish.ingredientIds.length > 0) {
          ingredientIds = [...existingDish.ingredientIds];
        }
      }

      const payload: DishDto = {
        name: v.name,
        preparedAt: this.toLocalDateTimeString(v.preparedAt),
        calories: Number(v.calories) || 0,
        protein: Number(v.protein) || 0,
        fat: Number(v.fat) || 0,
        carbohydrates: Number(v.carbohydrates) || 0,
        recipeIds,
        ingredientIds,
      };

      if (id != null) {
        await firstValueFrom(this.dishesService.update(id, payload));
        this.toastService.push('Dish updated', 'success');
      } else {
        await firstValueFrom(this.dishesService.create(payload));
        this.toastService.push('Dish created', 'success');
      }

      this.dialogVisible = false;
      await this.loadData();
    } finally {
      this.loadingService.hide();
    }
  }

  cancel() {
    this.dialogVisible = false;
    this.form.reset();
  }

  getRecipeName(id: string | undefined): string {
    if (id === null || id === undefined || id === '') {
      return '-';
    }
    const recipe = this.recipes.find((r) => r.id === id);
    return recipe ? recipe.name : id;
  }

  private toLocalDateTimeString(value: unknown): string {
    if (value === null || value === undefined) {
      throw new Error('PreparedAt is required');
    }

    let d: Date;
    if (value instanceof Date) {
      d = value;
    } else if (typeof value === 'string') {
      d = new Date(value);
    } else {
      throw new Error('Invalid preparedAt value');
    }

    if (isNaN(d.getTime())) {
      throw new Error('Invalid preparedAt date');
    }

    const pad = (n: number) => n.toString().padStart(2, '0');

    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T00:00:00`;
  }
}
