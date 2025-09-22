import { Component, OnInit } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Dish, DishesService } from '../../services/dishes-services/dishes-services';
import { TableModule} from 'primeng/table';
import { DatePipe, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-dishes',
  templateUrl: './dishes-component.html',
  imports: [
    TableModule,
    DatePipe,
    NgIf,
    NgForOf,
  ],
  styleUrls: ['./dishes-component.css'],
})
export class DishesComponent implements OnInit {
  dishes: Dish[] = [];

  constructor(private dishesService: DishesService) {}

  async ngOnInit() {
    this.dishes = await firstValueFrom(this.dishesService.getDishes());
    console.log('Dishes loaded:', this.dishes);
  }
}
