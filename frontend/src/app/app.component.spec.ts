import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AppComponent } from './app.component';
import { AuthService } from './core/auth.service';

describe('AppComponent', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    });
  });

  it('se cree et permet la deconnexion', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const cmp = fixture.componentInstance;
    fixture.detectChanges();
    expect(cmp.auth).toBeInstanceOf(AuthService);
    cmp.auth.logout();
    expect(cmp.auth.currentUser()).toBeNull();
  });
});
