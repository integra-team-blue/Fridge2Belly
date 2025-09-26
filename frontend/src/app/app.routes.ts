import { Routes } from '@angular/router';
import {DishesComponent} from './component/dishes-component/dishes-component';
import {RecipesComponent} from './component/recipes-component/recipes-component';
import {IngredientsComponent} from './component/ingredients-component/ingredients-component';
import {MealsComponent} from './component/meals-component/meals-component';
import {SampleDataComponent} from './component/sample-data-component/sample-data-component';

import { LoginComponent } from './component/auth-component/login.component';
import { SignupComponent } from './component/auth-component/signup.component';
import { authGuard } from './services/auth-services/auth.guard';
import { DashboardComponent } from './component/dashboard.component';

export const routes: Routes = [
  {
    path: 'dishes',
    component: DishesComponent
  },
  {
    path: 'recipes',
    component: RecipesComponent
  },
  {
    path: 'ingredients',
    component: IngredientsComponent
  },
  {
    path: 'meals',
    component: MealsComponent
  }
];
