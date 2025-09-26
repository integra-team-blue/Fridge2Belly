import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { NgForOf, NgIf, DatePipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { DatePicker } from 'primeng/datepicker';
import { InputNumber } from 'primeng/inputnumber';
import {
  DishesService,
  Dish,
  RecipeOption,
  CreateDishPayload,
} from '../../services/dishes-services/dishes-services';
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
    ConfirmDialog,
    Button,
    InputText,
    Select,
    DatePicker,
    InputNumber,
  ],
})
export class DishesComponent implements OnInit {
  dishes: Dish[] = [];
  recipes: RecipeOption[] = [];
  dialogVisible = false;

  private fb = inject(FormBuilder);
  private dishesService = inject(DishesService);
  public loading = inject(LoadingService);
  private toast = inject(ToastService);
  private confirm = inject(ConfirmationService);
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
    this.loading.show();
    try {
      const [d, r] = await Promise.all([
        firstValueFrom(this.dishesService.getDishes()),
        firstValueFrom(this.dishesService.getRecipes()),
      ]);
      this.dishes = d;
      this.recipes = r;
    } finally {
      this.loading.hide();
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

  onRowSelect(event: { data?: Dish | Dish[] }) {
    const dish = Array.isArray(event?.data) ? event.data[0] : event?.data;
    if (dish == null) {
      return;
    }
    this._editId.set(dish.id);
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

  onRightClick(event: MouseEvent, dish: Dish) {
    event.preventDefault();
    this.confirm.confirm({
      header: 'Delete dish',
      message: `Are you sure you want to delete "${dish.name}"?`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => this.deleteDish(dish.id),
    });
  }

  private async deleteDish(id: string) {
    this.loading.show();
    try {
      await firstValueFrom(this.dishesService.deleteDish(id));
      this.dishes = this.dishes.filter((d) => d.id !== id);
      this.toast.push('Dish deleted', 'success');
    } finally {
      this.loading.hide();
    }
  }

  async submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.show();
    try {
      const v = this.form.getRawValue();
      const recipeIds = typeof v.recipeId === 'string' && v.recipeId.length > 0 ? [v.recipeId] : [];

      const payload: CreateDishPayload = {
        name: String(v.name),
        preparedAt: this.toLocalDateTimeString(v.preparedAt),
        calories: Number(v.calories),
        protein: Number(v.protein),
        fat: Number(v.fat),
        carbohydrates: Number(v.carbohydrates),
        recipeIds,
        ingredientIds: [],
      };

      const id = this._editId();
      if (id != null && id !== '') {
        await firstValueFrom(this.dishesService.updateDish(id, payload));
        this.toast.push('Dish updated', 'success');
      } else {
        await firstValueFrom(this.dishesService.createDish(payload));
        this.toast.push('Dish created', 'success');
      }

      this.dialogVisible = false;
      await this.loadData();
    } finally {
      this.loading.hide();
    }
  }

  cancel() {
    this.dialogVisible = false;
    this.form.reset();
  }

  private toLocalDateTimeString(value: unknown): string {
    const d =
      value instanceof Date
        ? value
        : new Date(typeof value === 'string' && value ? value : Date.now());
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(
      d.getMinutes(),
    )}:${pad(d.getSeconds())}`;
  }
}
