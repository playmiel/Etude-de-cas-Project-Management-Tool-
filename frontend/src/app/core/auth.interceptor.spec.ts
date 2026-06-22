import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => httpMock.verify());

  it('n ajoute pas l en-tete sans utilisateur', () => {
    http.get('/api/projects').subscribe();
    const req = httpMock.expectOne('/api/projects');
    expect(req.request.headers.has('X-User-Id')).toBe(false);
    req.flush([]);
  });

  it('ajoute X-User-Id quand connecte', () => {
    auth.login('e@e.fr', 'pw').subscribe();
    httpMock.expectOne('/api/auth/login').flush({ id: 42, username: 'u', email: 'e@e.fr' });
    http.get('/api/projects').subscribe();
    const req = httpMock.expectOne('/api/projects');
    expect(req.request.headers.get('X-User-Id')).toBe('42');
    req.flush([]);
  });
});
