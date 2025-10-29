import { Component } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';
import { RouterLink } from '@angular/router';
import { Toast } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { SampleDataService } from '../../services/sample-data/sample-data-service';
import {Menu} from 'primeng/menu';
import { ImageModule } from 'primeng/image';

@Component({
  selector: 'app-navbar-component',
  standalone: true,
  templateUrl: './navbar-component.html',
  imports: [MenubarModule, RouterLink, ButtonModule, CommonModule, Menu, ImageModule],
  styleUrls: ['./navbar-component.css'],
})
export class NavbarComponent {
  items: MenuItem[] = [];

  constructor(private sampleDataService: SampleDataService) {}

  avatarItems: MenuItem[] = [
    { label: 'My Fridge', icon: 'pi pi-fw pi-box', routerLink: '/fridge' },
    { separator: true },
  ];

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
