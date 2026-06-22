import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

/**
 * Ajoute l'en-tete X-User-Id a chaque requete /api afin d'identifier
 * l'utilisateur courant cote backend (l'enonce n'exige pas Spring Security).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const userId = auth.userId();
  if (userId !== null) {
    req = req.clone({ setHeaders: { 'X-User-Id': String(userId) } });
  }
  return next(req);
};
