import { CanActivateFn } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const user = localStorage.getItem('user');

  // If not logged in → redirect to /login
  if (!user) {
    window.location.href = "/login";
    return false;
  }

  return true;
};
