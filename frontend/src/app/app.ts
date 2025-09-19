import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './component/navbar-component/navbar-component';
import { Toast } from 'primeng/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, Toast],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('frontend');
}
