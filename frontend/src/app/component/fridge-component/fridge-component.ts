import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ContextMenu } from 'primeng/contextmenu';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import {
  Ingredient,
  IngredientsService,
} from '../../services/ingredients-services/ingredients-service';
import {
  IngredientsUserService,
  UserIngredientUpdate,
} from '../../services/ingredientsUser-service/ingredientsUser-service';
import { firstValueFrom } from 'rxjs';
import { Select } from 'primeng/select';
import { MultiSelect } from 'primeng/multiselect';
import { AuthService } from '../../services/auth-services/auth.service';

@Component({
  selector: 'app-fridge',
  standalone: true,
  templateUrl: './fridge-component.html',
  styleUrls: ['./fridge-component.css'],
  imports: [
    FormsModule,
    CommonModule,
    CardModule,
    ButtonModule,
    InputTextModule,
    DialogModule,
    ConfirmDialog,
    ContextMenu,
    MultiSelect,
    Select,
  ],
})
export class FridgeComponent {
  maxPerShelf = 5;
  shelves: Array<Array<Ingredient>> = [];
  displayDialog = false;
  menuItems: MenuItem[];
  selectedIngredient: Ingredient | null = null;
  selectedShelfIndex: number | null = null;
  selectedIngredientIndex: number | null = null;
  allIngredients: Ingredient[] = [];
  allIngredientsFromUser: Ingredient[] = [];
  selectedIngredientToAdd: Ingredient[] = [];
  displayEditDialog = false;
  ingredientToEdit: Ingredient | null = null;
  editedQuantity = 0;
  editedUnit: string = '';
  editedExpirationDate: string = '';
  editedCalories = 0;
  editedProtein = 0;
  editedFat = 0;
  editedCarbohydrates = 0;

  unitOptions = [
    { name: 'kg', code: 'kg' },
    { name: 'g', code: 'g' },
    { name: 'l', code: 'l' },
    { name: 'ml', code: 'ml' },
    { name: 'pcs', code: 'pcs' },
  ];

  constructor(
    private confirmationService: ConfirmationService,
    private ingredientsService: IngredientsService,
    private ingredientsUserService: IngredientsUserService,
    private authService: AuthService,
    private messageService: MessageService,
  ) {
    for (let i = 0; i < 5; i++) {
      this.addShelf();
    }

    this.menuItems = [
      {
        label: 'Edit',
        icon: 'pi pi-pencil',
        command: () => {
          if (
            this.selectedIngredient &&
            this.selectedShelfIndex !== null &&
            this.selectedIngredientIndex !== null
          ) {
            this.openEditDialog(this.selectedIngredient);
          }
        },
      },
      {
        label: 'Delete',
        icon: 'pi pi-times',
        command: () => {
          if (
            this.selectedIngredient &&
            this.selectedShelfIndex !== null &&
            this.selectedIngredientIndex !== null
          ) {
            this.confirmDelete(
              this.selectedShelfIndex,
              this.selectedIngredientIndex,
              this.selectedIngredient,
            );
          }
        },
      },
    ];
  }

  async saveIngredientEdit() {
    if (this.ingredientToEdit == null) {
      return;
    }

    try {
      const user = this.authService.getCurrentUser();
      if (user == null) {
        return;
      }

      const dto: UserIngredientUpdate = {
        quantity: this.editedQuantity,
        unit: this.editedUnit,
        expirationDate: new Date(this.editedExpirationDate), // convert string -> Date
        calories: this.editedCalories,
        protein: this.editedProtein,
        fat: this.editedFat,
        carbohydrates: this.editedCarbohydrates,
      };

      await firstValueFrom(
        this.ingredientsUserService.updateIngredientFromUser(this.ingredientToEdit.id!, dto),
      );

      for (let shelf of this.shelves) {
        const index = shelf.findIndex((i) => i.id === this.ingredientToEdit!.id);
        if (index !== -1) {
          shelf[index] = {
            ...this.ingredientToEdit!,
            ...dto,
          };
          break;
        }
      }

      this.displayEditDialog = false;
      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'The ingredient was edited successfully.',
      });
    } catch (err) {
      this.messageService.add({
        severity: 'Error',
        summary: 'Error',
        detail: 'Failed to edit the ingredient. Please try again.',
      });
    }
  }

  openEditDialog(ingredient: Ingredient) {
    this.ingredientToEdit = { ...ingredient };
    this.editedQuantity = ingredient.quantity ?? 0;
    this.editedUnit = ingredient.unit;
    this.editedExpirationDate =
      ingredient.expirationDate != null
        ? new Date(ingredient.expirationDate).toISOString().split('T')[0]
        : '';
    this.editedCalories = ingredient.calories ?? 0;
    this.editedProtein = ingredient.protein ?? 0;
    this.editedFat = ingredient.fat ?? 0;
    this.editedCarbohydrates = ingredient.carbohydrates ?? 0;
    this.displayEditDialog = true;
  }

  async ngOnInit() {
    try {
      this.allIngredients = await firstValueFrom(this.ingredientsService.getIngredients());
      await this.loadUserIngredients();
    } catch (err) {
      console.error('Failed to load ingredients', err);
    }
  }

  async addIngredientsForUser(ingredients: Ingredient[]) {
    if (ingredients == null || ingredients.length === 0) {
      return;
    }

    const allNewIngredients = ingredients.map((i) => ({ ...i }));

    this.shelves = [];

    let tempShelf: Ingredient[] = [];

    for (let i = 0; i < allNewIngredients.length; i++) {
      tempShelf.push(allNewIngredients[i]);
      if (tempShelf.length === this.maxPerShelf || i === allNewIngredients.length - 1) {
        this.shelves.push(tempShelf);
        tempShelf = [];
      }
    }

    while (this.shelves.length < 5) {
      this.shelves.push([]);
    }
  }

  async addSelectedIngredient() {
    const user = this.authService.getCurrentUser();
    if (this.selectedIngredientToAdd == null || this.selectedIngredientToAdd.length === 0) {
      return;
    }

    for (let ingredient of this.selectedIngredientToAdd) {
      let added = false;

      for (let shelf of this.shelves) {
        if (shelf.length < this.maxPerShelf) {
          shelf.push({ ...ingredient });
          added = true;
          break;
        }
      }
      if (added == null) {
        this.addShelf();
        this.shelves[this.shelves.length - 1].push({ ...ingredient });
      }

      try {
        await firstValueFrom(
          this.ingredientsUserService.addIngredientToUser(ingredient.id!, user?.id),
        );
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

    this.selectedIngredientToAdd = [];
  }

  async loadUserIngredients() {
    const user = this.authService.getCurrentUser();
    if (user == null) {
      return;
    }

    try {
      this.allIngredientsFromUser = await firstValueFrom(
        this.ingredientsUserService.getIngredientsForUser(user.id),
      );

      this.addIngredientsForUser(this.allIngredientsFromUser);
    } catch (err) {
      console.error('Failed to load user ingredients', err);
    }
  }

  onRightClick(
    event: MouseEvent,
    menu: any,
    shelfIndex: number,
    ingredientIndex: number,
    ingredient: Ingredient,
  ) {
    event.preventDefault();
    this.selectedIngredient = ingredient;
    this.selectedShelfIndex = shelfIndex;
    this.selectedIngredientIndex = ingredientIndex;
    menu.show(event);
  }

  showDetails(ingredient: Ingredient) {
    this.selectedIngredient = ingredient;
    this.displayDialog = true;
  }

  addShelf() {
    this.shelves.push([]);
  }

  async removeIngredient(shelfIndex: number, ingredientIndex: number) {
    const user = this.authService.getCurrentUser();
    if (user == null) {
      return;
    }

    const ingredientUser = this.shelves[shelfIndex][ingredientIndex];
    if (ingredientUser?.id == null) {
      return;
    }

    try {
      await firstValueFrom(this.ingredientsUserService.removeIngredientFromUser(ingredientUser.id));
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

    this.shelves[shelfIndex].splice(ingredientIndex, 1);

    for (let i = shelfIndex; i < this.shelves.length - 1; i++) {
      if (this.shelves[i + 1].length > 0) {
        this.shelves[i].push(this.shelves[i + 1].shift()!);
      }
    }

    while (this.shelves.length > 5 && this.shelves[this.shelves.length - 1].length === 0) {
      this.shelves.pop();
    }
  }

  confirmDelete(shelfIndex: number, ingredientIndex: number, ingredient: Ingredient | null) {
    if (ingredient == null) {
      return;
    }

    this.confirmationService.confirm({
      message: `Are you sure you want to delete "${ingredient.name}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Yes',
      rejectLabel: 'No',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => this.removeIngredient(shelfIndex, ingredientIndex),
    });
  }
}
