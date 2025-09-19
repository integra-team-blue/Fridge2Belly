import { Component } from '@angular/core';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar-component',
  standalone: true,
  templateUrl: './navbar-component.html',
  imports: [
    MenubarModule,
    RouterLink
  ],
  styleUrls: ['./navbar-component.css']
})
export class NavbarComponent {
  items: MenuItem[] = [];

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
}
