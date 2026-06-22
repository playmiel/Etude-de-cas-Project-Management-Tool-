import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    });
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  afterEach(() => httpMock.verify());

  it('inscrit avec succes', () => {
    const cmp = TestBed.createComponent(RegisterComponent).componentInstance;
    cmp.username = 'u'; cmp.email = 'e@e.fr'; cmp.password = 'pw';
    cmp.submit();
    httpMock.expectOne('/api/auth/register').flush({ id: 1, username: 'u', email: 'e@e.fr' });
    expect(cmp.success).toBe(true);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('gere une erreur de conflit', () => {
    const cmp = TestBed.createComponent(RegisterComponent).componentInstance;
    cmp.username = 'u'; cmp.email = 'e@e.fr'; cmp.password = 'pw';
    cmp.submit();
    httpMock.expectOne('/api/auth/register').flush(
      { message: 'E-mail deja utilise' }, { status: 409, statusText: 'Conflict' });
    expect(cmp.error).toBe('E-mail deja utilise');
  });
});
