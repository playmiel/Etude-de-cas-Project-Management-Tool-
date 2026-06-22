import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProjectDetailComponent } from './project-detail.component';

describe('ProjectDetailComponent', () => {
  let httpMock: HttpTestingController;

  function setup() {
    const fixture = TestBed.createComponent(ProjectDetailComponent);
    const cmp = fixture.componentInstance;
    fixture.detectChanges(); // ngOnInit
    httpMock.expectOne('/api/projects/1').flush({ id: 1, name: 'P', currentUserRole: 'ADMIN' });
    httpMock.expectOne('/api/projects/1/members').flush([
      { memberId: 1, userId: 1, username: 'a', email: 'a@a.fr', role: 'ADMIN' }
    ]);
    httpMock.expectOne('/api/projects/1/tasks').flush([]);
    return cmp;
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProjectDetailComponent],
      providers: [
        provideRouter([]), provideHttpClient(), provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } }
      ]
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('charge le projet, les membres et les taches', () => {
    const cmp = setup();
    expect(cmp.project?.name).toBe('P');
    expect(cmp.members.length).toBe(1);
    expect(cmp.canManage()).toBe(true);
    expect(cmp.canEdit()).toBe(true);
  });

  it('invite un membre et change un role', () => {
    const cmp = setup();
    cmp.inviteEmail = 'b@b.fr';
    cmp.invite();
    httpMock.expectOne((r) => r.url === '/api/projects/1/members' && r.method === 'POST')
      .flush({ memberId: 2, userId: 2, username: 'b', email: 'b@b.fr', role: 'MEMBER' });
    httpMock.expectOne('/api/projects/1/members').flush([]); // reload
    expect(cmp.inviteEmail).toBe('');

    cmp.changeRole({ memberId: 2, userId: 2, username: 'b', email: 'b@b.fr', role: 'MEMBER' }, 'OBSERVER');
    httpMock.expectOne('/api/projects/1/members/2/role')
      .flush({ memberId: 2, userId: 2, username: 'b', email: 'b@b.fr', role: 'OBSERVER' });
    httpMock.expectOne('/api/projects/1/members').flush([]); // reload
  });

  it('gere une erreur d invitation', () => {
    const cmp = setup();
    cmp.inviteEmail = 'x@x.fr';
    cmp.invite();
    httpMock.expectOne('/api/projects/1/members')
      .flush({ message: 'Utilisateur introuvable' }, { status: 404, statusText: 'Not Found' });
    expect(cmp.inviteError).toBe('Utilisateur introuvable');
  });

  it('cree une tache', () => {
    const cmp = setup();
    cmp.taskName = 'Tache 1';
    cmp.createTask();
    httpMock.expectOne((r) => r.url === '/api/projects/1/tasks' && r.method === 'POST')
      .flush({ id: 5, name: 'Tache 1', priority: 'MEDIUM', status: 'TODO', projectId: 1 });
    httpMock.expectOne('/api/projects/1/tasks').flush([]); // reload
    expect(cmp.taskName).toBe('');
  });
});
