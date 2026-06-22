import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('authGuard', () => {
  let auth: AuthService;
  let router: Router;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    auth = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    httpMock = TestBed.inject(HttpTestingController);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  const run = () => TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));

  it('refuse et redirige si non connecte', () => {
    expect(run()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('autorise si connecte', () => {
    auth.login('e@e.fr', 'pw').subscribe();
    httpMock.expectOne('/api/auth/login').flush({ id: 1, username: 'u', email: 'e@e.fr' });
    expect(run()).toBe(true);
  });
});
