import { Component } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';
import { RouterLink } from '@angular/router';
import { Toast } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { SampleDataService } from '../../services/sample-data/sample-data-service';

@Component({
  selector: 'app-navbar-component',
  standalone: true,
  templateUrl: './navbar-component.html',
  imports: [MenubarModule, RouterLink, Toast, ButtonModule, CommonModule],
  styleUrls: ['./navbar-component.css'],
})
export class NavbarComponent {
  items: MenuItem[] = [];

  constructor(private sampleDataService: SampleDataService) {}

  ngOnInit() {
    this.items = [
      {
        label: 'Meals',
        routerLink: '/meals',
      },
      {
        label: 'Dishes',
        routerLink: '/dishes',
      },
      {
        label: 'Recipes',
        routerLink: '/recipes',
      },
      {
        label: 'Ingredients',
        routerLink: '/ingredients',
      },
    ];
  }

  async generateSampleData() {
    await this.sampleDataService.generateSampleDataWithNotifications();
  }

  get isLoading(): boolean {
    return this.sampleDataService.isLoading;
  }
}
