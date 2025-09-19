import { Component, OnInit } from '@angular/core';
import { DishesService, Dish } from '../../services/dishes-services/dishes-services';
import {TableModule} from 'primeng/table';
import {DatePipe, NgForOf} from '@angular/common';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-dishes',
  templateUrl: './dishes-component.html',
  imports: [
    TableModule,
    DatePipe,
    NgForOf
  ],
  styleUrls: ['./dishes-component.css']
})
export class DishesComponent implements OnInit {
  dishes: Dish[] = [];

  constructor(private dishesService: DishesService) {}

  async ngOnInit() {
    this.dishes = await firstValueFrom(this.dishesService.getDishes());
    console.log(this.dishes);
  }
}
