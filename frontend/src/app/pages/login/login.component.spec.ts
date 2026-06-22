import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    });
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  afterEach(() => httpMock.verify());

  it('se connecte avec succes', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const cmp = fixture.componentInstance;
    cmp.email = 'e@e.fr';
    cmp.password = 'pw';
    cmp.submit();
    httpMock.expectOne('/api/auth/login').flush({ id: 1, username: 'u', email: 'e@e.fr' });
    expect(router.navigate).toHaveBeenCalledWith(['/projects']);
  });

  it('affiche une erreur si identifiants invalides', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    const cmp = fixture.componentInstance;
    cmp.email = 'e@e.fr';
    cmp.password = 'bad';
    cmp.submit();
    httpMock.expectOne('/api/auth/login').flush('nope', { status: 401, statusText: 'Unauthorized' });
    expect(cmp.error).toBeTruthy();
  });
});
