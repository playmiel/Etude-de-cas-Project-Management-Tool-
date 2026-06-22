import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('inscrit un utilisateur', () => {
    service.register('u', 'e@e.fr', 'pw').subscribe((u) => expect(u.username).toBe('u'));
    const req = httpMock.expectOne('/api/auth/register');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1, username: 'u', email: 'e@e.fr' });
  });

  it('connecte puis deconnecte un utilisateur', () => {
    expect(service.isAuthenticated()).toBe(false);
    expect(service.userId()).toBeNull();
    service.login('e@e.fr', 'pw').subscribe();
    httpMock.expectOne('/api/auth/login').flush({ id: 7, username: 'u', email: 'e@e.fr' });
    expect(service.isAuthenticated()).toBe(true);
    expect(service.userId()).toBe(7);
    service.logout();
    expect(service.currentUser()).toBeNull();
  });

  it('restaure l utilisateur depuis le stockage local', () => {
    localStorage.setItem('pmt_user', JSON.stringify({ id: 3, username: 'x', email: 'x@x.fr' }));
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const svc = TestBed.inject(AuthService);
    expect(svc.userId()).toBe(3);
  });
});
