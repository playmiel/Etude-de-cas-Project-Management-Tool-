import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TaskService } from './task.service';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('liste les taches', () => {
    service.list(1).subscribe((t) => expect(t.length).toBe(1));
    httpMock.expectOne('/api/projects/1/tasks').flush([{ id: 1, name: 'T' }]);
  });

  it('recupere le tableau de bord', () => {
    service.dashboard(1).subscribe((c) => expect(c.length).toBe(3));
    httpMock.expectOne('/api/projects/1/dashboard').flush([
      { status: 'TODO', tasks: [] }, { status: 'IN_PROGRESS', tasks: [] }, { status: 'DONE', tasks: [] }
    ]);
  });

  it('cree une tache', () => {
    service.create(1, { name: 'T', priority: 'HIGH' }).subscribe((t) => expect(t.name).toBe('T'));
    const req = httpMock.expectOne('/api/projects/1/tasks');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1, name: 'T' });
  });

  it('recupere, met a jour et assigne une tache', () => {
    service.get(9).subscribe((t) => expect(t.id).toBe(9));
    httpMock.expectOne('/api/tasks/9').flush({ id: 9, name: 'T' });

    service.update(9, { status: 'DONE' }).subscribe((t) => expect(t.status).toBe('DONE'));
    const up = httpMock.expectOne('/api/tasks/9');
    expect(up.request.method).toBe('PUT');
    up.flush({ id: 9, name: 'T', status: 'DONE' });

    service.assign(9, 2).subscribe();
    const pa = httpMock.expectOne('/api/tasks/9/assignee');
    expect(pa.request.method).toBe('PATCH');
    pa.flush({ id: 9, name: 'T' });
  });

  it('recupere l historique', () => {
    service.history(9).subscribe((h) => expect(h.length).toBe(1));
    httpMock.expectOne('/api/tasks/9/history').flush([
      { id: 1, changeDescription: 'creee', changedByUsername: 'u', changedAt: 'now' }
    ]);
  });
});
