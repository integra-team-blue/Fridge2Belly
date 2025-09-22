import { Routes } from '@angular/router';

import { DishesComponent } from './component/dishes-component/dishes-component';
import { RecipesComponent } from './component/recipes-component/recipes-component';
import { IngredientsComponent } from './component/ingredients-component/ingredients-component';
import { MealsComponent } from './component/meals-component/meals-component';

import { LoginComponent } from './component/auth-component/login.component';
import { SignupComponent } from './component/auth-component/signup.component';
import { authGuard } from './services/auth-services/auth.guard';
import { DashboardComponent } from './component/dashboard.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'dashboard', canActivate: [authGuard], component: DashboardComponent },
  { path: 'dishes', canActivate: [authGuard], component: DishesComponent },
  { path: 'recipes', canActivate: [authGuard], component: RecipesComponent },
  { path: 'ingredients', canActivate: [authGuard], component: IngredientsComponent },
  { path: 'meals', canActivate: [authGuard], component: MealsComponent },
  { path: '**', redirectTo: 'login' }
];
