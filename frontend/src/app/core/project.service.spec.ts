import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProjectService } from './project.service';

describe('ProjectService', () => {
  let service: ProjectService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(ProjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('liste les projets', () => {
    service.list().subscribe((p) => expect(p.length).toBe(1));
    httpMock.expectOne('/api/projects').flush([{ id: 1, name: 'P', currentUserRole: 'ADMIN' }]);
  });

  it('recupere un projet', () => {
    service.get(5).subscribe((p) => expect(p.id).toBe(5));
    httpMock.expectOne('/api/projects/5').flush({ id: 5, name: 'P', currentUserRole: 'MEMBER' });
  });

  it('cree un projet', () => {
    service.create('P', 'd', '2026-01-01').subscribe((p) => expect(p.name).toBe('P'));
    const req = httpMock.expectOne('/api/projects');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1, name: 'P', currentUserRole: 'ADMIN' });
  });

  it('gere les membres (liste, invitation, role)', () => {
    service.members(1).subscribe((m) => expect(m.length).toBe(1));
    httpMock.expectOne('/api/projects/1/members').flush([
      { memberId: 1, userId: 2, username: 'b', email: 'b@b.fr', role: 'MEMBER' }
    ]);

    service.invite(1, 'b@b.fr', 'MEMBER').subscribe((m) => expect(m.role).toBe('MEMBER'));
    httpMock.expectOne('/api/projects/1/members').flush(
      { memberId: 1, userId: 2, username: 'b', email: 'b@b.fr', role: 'MEMBER' });

    service.changeRole(1, 1, 'OBSERVER').subscribe((m) => expect(m.role).toBe('OBSERVER'));
    httpMock.expectOne('/api/projects/1/members/1/role').flush(
      { memberId: 1, userId: 2, username: 'b', email: 'b@b.fr', role: 'OBSERVER' });
  });
});
