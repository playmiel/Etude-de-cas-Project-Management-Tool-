import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { DashboardComponent } from './dashboard.component';

describe('DashboardComponent', () => {
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        provideRouter([]), provideHttpClient(), provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } }
      ]
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('charge les colonnes et traduit les statuts', () => {
    const fixture = TestBed.createComponent(DashboardComponent);
    const cmp = fixture.componentInstance;
    fixture.detectChanges();
    httpMock.expectOne('/api/projects/1/dashboard').flush([
      { status: 'TODO', tasks: [{ id: 1, name: 'a', priority: 'LOW', status: 'TODO', projectId: 1 }] },
      { status: 'IN_PROGRESS', tasks: [] },
      { status: 'DONE', tasks: [] }
    ]);
    expect(cmp.columns.length).toBe(3);
    expect(cmp.label('TODO')).toBe('A faire');
    expect(cmp.label('IN_PROGRESS')).toBe('En cours');
    expect(cmp.label('DONE')).toBe('Termine');
    expect(cmp.label('AUTRE')).toBe('AUTRE');
  });
});
