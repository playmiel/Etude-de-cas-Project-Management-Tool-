-- =====================================================================
--  Donnees de test PMT (a executer apres schema.sql)
--  Mot de passe en clair pour les 3 comptes : password123
-- =====================================================================

-- ----- Utilisateurs (mots de passe chiffres en BCrypt) -----
INSERT INTO users (id, username, email, password_hash) VALUES
  (1, 'alice', 'alice@pmt.local', '$2b$10$N92RBLi4Kl.NPaetZjY8zOxntR8slRXQXI9/ExHGGSn8PyguxNVwi'),
  (2, 'bob',   'bob@pmt.local',   '$2b$10$DS9d.6ovIkP1nOZTc6g6q.6dtkPnBl0x4bod4hCGn29wQglUWNLRm'),
  (3, 'carol', 'carol@pmt.local', '$2b$10$l6DQzB6n5GgZt/RmzjWY8.DcCP39G7eBqeC5kvc/ii.25GNYyHDc6');

-- ----- Projet de demonstration -----
INSERT INTO projects (id, name, description, start_date) VALUES
  (1, 'Plateforme PMT', 'Outil de gestion de projets collaboratif', DATE '2026-01-15');

-- ----- Membres : alice ADMIN (creatrice), bob MEMBER, carol OBSERVER -----
INSERT INTO project_members (id, project_id, user_id, role) VALUES
  (1, 1, 1, 'ADMIN'),
  (2, 1, 2, 'MEMBER'),
  (3, 1, 3, 'OBSERVER');

-- ----- Taches -----
INSERT INTO tasks (id, project_id, name, description, due_date, end_date, priority, status, assignee_id) VALUES
  (1, 1, 'Concevoir le schema de base de donnees', 'Modele relationnel des entites', DATE '2026-01-20', DATE '2026-01-19', 'HIGH',   'DONE',        1),
  (2, 1, 'Implementer l''API d''authentification', 'Inscription et connexion',       DATE '2026-01-25', NULL,            'HIGH',   'IN_PROGRESS', 2),
  (3, 1, 'Creer le tableau de bord Angular',       'Regroupement des taches par statut', DATE '2026-02-05', NULL,        'MEDIUM', 'TODO',        2),
  (4, 1, 'Mettre en place la pipeline CI/CD',      'GitHub Actions + Docker Hub',     DATE '2026-02-10', NULL,            'LOW',    'TODO',        NULL);

-- ----- Historique -----
INSERT INTO task_history (id, task_id, changed_by, change_description) VALUES
  (1, 1, 1, 'Tache creee : ''Concevoir le schema de base de donnees'''),
  (2, 1, 1, 'statut: TODO -> DONE ; date de fin -> 2026-01-19'),
  (3, 2, 1, 'Tache creee : ''Implementer l''''API d''''authentification'''),
  (4, 2, 2, 'statut: TODO -> IN_PROGRESS');

-- ----- Notifications -----
INSERT INTO notifications (id, recipient_id, task_id, message) VALUES
  (1, 1, 1, 'Une tache vous a ete assignee : ''Concevoir le schema de base de donnees''.'),
  (2, 2, 2, 'Une tache vous a ete assignee : ''Implementer l''''API d''''authentification''.'),
  (3, 2, 3, 'Une tache vous a ete assignee : ''Creer le tableau de bord Angular''.');

-- ----- Synchronisation des sequences d'identite -----
SELECT setval(pg_get_serial_sequence('users', 'id'),            (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('projects', 'id'),         (SELECT MAX(id) FROM projects));
SELECT setval(pg_get_serial_sequence('project_members', 'id'),  (SELECT MAX(id) FROM project_members));
SELECT setval(pg_get_serial_sequence('tasks', 'id'),            (SELECT MAX(id) FROM tasks));
SELECT setval(pg_get_serial_sequence('task_history', 'id'),     (SELECT MAX(id) FROM task_history));
SELECT setval(pg_get_serial_sequence('notifications', 'id'),    (SELECT MAX(id) FROM notifications));
