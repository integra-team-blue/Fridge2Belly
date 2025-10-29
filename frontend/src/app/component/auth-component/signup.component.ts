import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { catchError, map, of, switchMap, timer } from 'rxjs';
import { AuthService } from '../../services/auth-services/auth.service';
import { ToastService } from '../../services/toast.service';
import { LoadingService } from '../../services/loading.service';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { buildErrorMessage } from '../../utils/error-utils';

const strong = (v: string) => v.length >= 8 && /[A-Z]/.test(v) && /[a-z]/.test(v) && /\d/.test(v);

@Component({
  standalone: true,
  selector: 'app-signup',
  imports: [ReactiveFormsModule, RouterLink, NgIf, InputTextModule, PasswordModule, ButtonModule],
  template: `
    <div class="auth-container">
      <section class="auth-card">
        <h1>Sign up</h1>
        <p>Create a new account.</p>

        <form [formGroup]="form" (ngSubmit)="submit()">
          <div class="field">
            <label>Email</label>
            <input type="email" pInputText formControlName="email" />
            <small *ngIf="email.touched && email.hasError('required')">Email is required.</small>
            <small *ngIf="email.touched && email.hasError('email')"
              >Please enter a valid email.</small
            >
            <small *ngIf="email.touched && email.hasError('taken')">Email already used.</small>
          </div>

          <div class="field">
            <label>Username</label>
            <input type="text" pInputText formControlName="username" />
            <small *ngIf="username.touched && username.hasError('required')"
              >Username is required.</small
            >
            <small *ngIf="username.touched && username.hasError('minlength')"
              >Minimum 3 characters.</small
            >
            <small *ngIf="username.touched && username.hasError('taken')"
              >Username already taken.</small
            >
          </div>

          <div class="field">
            <label>Password</label>
            <p-password
              formControlName="password"
              [feedback]="true"
              [toggleMask]="true"
            ></p-password>
            <small *ngIf="password.touched && password.errors?.['weak']"
              >At least 8 chars, upper & lower case, and a number.</small
            >
            <small *ngIf="password.touched && password.hasError('required')"
              >Password is required.</small
            >
          </div>

          <div class="field">
            <label>Confirm password</label>
            <p-password
              formControlName="confirmPassword"
              [feedback]="false"
              [toggleMask]="true"
            ></p-password>
            <small *ngIf="confirmPassword.touched && confirmPassword.errors?.['mismatch']"
              >Passwords do not match.</small
            >
            <small *ngIf="confirmPassword.touched && confirmPassword.hasError('required')"
              >Confirm password is required.</small
            >
          </div>

          <button
            pButton
            type="submit"
            [disabled]="loading.active() || form.pending || form.invalid"
            [label]="loading.active() ? 'Creating account…' : 'Create account'"
          ></button>
        </form>

        <p class="switch-link">
          Already have an account?
          <a routerLink="/login">Log in</a>
        </p>
      </section>
    </div>
  `,
  styleUrls: ['./auth-styles.css'],
})
export class SignupComponent {
  private formBuilder = inject(FormBuilder);
  private auth = inject(AuthService);
  private toast = inject(ToastService);
  loading = inject(LoadingService);
  private router = inject(Router);

  form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email], [this.emailTakenValidator()]],
    username: ['', [Validators.required, Validators.minLength(3)], [this.usernameTakenValidator()]],
    password: [
      '',
      [
        Validators.required,
        (c: AbstractControl) => (strong(c.value ?? '') ? null : { weak: true }),
      ],
    ],
    confirmPassword: ['', [Validators.required]],
  });

  get email() {
    return this.form.get('email')!;
  }
  get username() {
    return this.form.get('username')!;
  }
  get password() {
    return this.form.get('password')!;
  }
  get confirmPassword() {
    return this.form.get('confirmPassword')!;
  }

  private usernameTakenValidator(): AsyncValidatorFn {
    return (control: AbstractControl) => {
      const value = control.value;
      if (value === null || value === undefined || value.trim() === '') {
        return of(null);
      }
      return timer(300).pipe(
        switchMap(() => this.auth.isUsernameTaken(value)),
        map((taken) => (taken ? ({ taken: true } as ValidationErrors) : null)),
        catchError(() => of(null)),
      );
    };
  }

  private emailTakenValidator(): AsyncValidatorFn {
    return (control: AbstractControl) => {
      const value = control.value;
      if (value === null || value === undefined || value.trim() === '') {
        return of(null);
      }
      return timer(300).pipe(
        switchMap(() => this.auth.isEmailTaken(value)),
        map((taken) => (taken ? ({ taken: true } as ValidationErrors) : null)),
        catchError(() => of(null)),
      );
    };
  }

  async submit() {
    const v = this.form.getRawValue();
    if (v.password !== v.confirmPassword) {
      this.confirmPassword.setErrors({ mismatch: true });
    }
    if (this.form.pending || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.show();
    try {
      await this.auth.signup({ username: v.username, email: v.email });
      this.toast.push('Account created. Welcome!', 'success');
      this.router.navigateByUrl('/dishes');
    } catch (err) {
      this.toast.push(buildErrorMessage(err, 'Signup failed'), 'error');
    } finally {
      this.loading.hide();
    }
  }
}
