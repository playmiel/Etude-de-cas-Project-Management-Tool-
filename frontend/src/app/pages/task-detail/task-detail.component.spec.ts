import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TaskDetailComponent } from './task-detail.component';

describe('TaskDetailComponent', () => {
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TaskDetailComponent],
      providers: [
        provideRouter([]), provideHttpClient(), provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '9' } } } }
      ]
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('charge la tache et son historique, puis enregistre', () => {
    const fixture = TestBed.createComponent(TaskDetailComponent);
    const cmp = fixture.componentInstance;
    fixture.detectChanges();
    httpMock.expectOne('/api/tasks/9').flush(
      { id: 9, name: 'T', priority: 'LOW', status: 'TODO', projectId: 1 });
    httpMock.expectOne('/api/tasks/9/history').flush([]);
    expect(cmp.task?.id).toBe(9);

    cmp.save();
    httpMock.expectOne((r) => r.url === '/api/tasks/9' && r.method === 'PUT')
      .flush({ id: 9, name: 'T', priority: 'HIGH', status: 'DONE', projectId: 1 });
    httpMock.expectOne('/api/tasks/9/history').flush([]);
    expect(cmp.saved).toBe(true);
  });

  it('save ne fait rien si la tache est absente', () => {
    const fixture = TestBed.createComponent(TaskDetailComponent);
    const cmp = fixture.componentInstance;
    fixture.detectChanges();
    httpMock.expectOne('/api/tasks/9').flush({ id: 9, name: 'T', priority: 'LOW', status: 'TODO', projectId: 1 });
    httpMock.expectOne('/api/tasks/9/history').flush([]);
    cmp.task = undefined;
    cmp.save();
    expect(cmp.saved).toBe(false);
  });
});
