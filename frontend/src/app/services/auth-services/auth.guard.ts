import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);

  const token = localStorage.getItem('token');
  const hasToken = typeof token === 'string' && token.trim().length > 0;

  if (hasToken === false) {
    router.navigateByUrl('/login');
    return false;
  }
  return true;
};
