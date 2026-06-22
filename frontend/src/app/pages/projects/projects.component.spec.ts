import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProjectsComponent } from './projects.component';

describe('ProjectsComponent', () => {
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProjectsComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('charge les projets puis en cree un', () => {
    const fixture = TestBed.createComponent(ProjectsComponent);
    const cmp = fixture.componentInstance;
    fixture.detectChanges(); // ngOnInit -> list
    httpMock.expectOne('/api/projects').flush([{ id: 1, name: 'P', currentUserRole: 'ADMIN' }]);
    expect(cmp.projects.length).toBe(1);

    cmp.name = 'Nouveau';
    cmp.create();
    httpMock.expectOne((r) => r.url === '/api/projects' && r.method === 'POST')
      .flush({ id: 2, name: 'Nouveau', currentUserRole: 'ADMIN' });
    // reload
    httpMock.expectOne('/api/projects').flush([]);
    expect(cmp.name).toBe('');
  });
});
