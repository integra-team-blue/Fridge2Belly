import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../services/auth-services/auth.service';
import { ToastService } from '../../services/toast.service';
import { LoadingService } from '../../services/loading.service';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { buildErrorMessage } from '../../utils/error-utils';

@Component({
  standalone: true,
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, NgIf, InputTextModule, PasswordModule, ButtonModule],
  template: `
    <div class="auth-container">
      <section class="auth-card">
        <h1>Login</h1>
        <p>Access your account.</p>

        <form [formGroup]="form" (ngSubmit)="submit()">
          <div class="field">
            <label>Email</label>
            <input type="email" pInputText formControlName="email" />
            <small *ngIf="email.touched && email.invalid">Please enter a valid email.</small>
          </div>

          <div class="field">
            <label>Password</label>
            <p-password
              formControlName="password"
              [feedback]="false"
              [toggleMask]="true"
            ></p-password>
            <small *ngIf="password.touched && password.hasError('required')"
              >Password is required.</small
            >
          </div>

          <button
            pButton
            type="submit"
            [disabled]="loading.active() || form.invalid"
            [label]="loading.active() ? 'Signing in…' : 'Sign in'"
          ></button>
        </form>

        <p class="switch-link">
          Don’t have an account?
          <a routerLink="/signup">Create one</a>
        </p>
      </section>
    </div>
  `,
  styleUrls: ['./auth-styles.css'],
})
export class LoginComponent {
  private formBuilder = inject(FormBuilder);
  private auth = inject(AuthService);
  private toast = inject(ToastService);
  loading = inject(LoadingService);
  private router = inject(Router);

  form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  get email() {
    return this.form.get('email')!;
  }
  get password() {
    return this.form.get('password')!;
  }

  async submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.show();
    try {
      await this.auth.login(this.form.getRawValue());
      const msg = 'Logged in successfully';
      this.toast.push(msg, 'success');
      this.router.navigateByUrl('/dishes');
    } catch (err) {
      this.toast.push(buildErrorMessage(err, 'Login failed'), 'error');
    } finally {
      this.loading.hide();
    }
  }
}
