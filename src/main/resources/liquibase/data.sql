insert into project(title, description, created, modified)
values ('Project1', 'ProjectDescription1', now(), now()),
       ('Project2', 'ProjectDescription2', now(), now());

insert into task(project_id, title, description, status, priority, created, modified)
values (1, 'Task1', 'TaskDescription1', 'NEW', 1, now(), now()),
       (1, 'Task2', 'TaskDescription2', 'IN_PROGRESS', 2, now(), now()),
       (1, 'Task3', 'TaskDescription3', 'CLOSED', 3, now(), now()),
       (2, 'Task4', 'TaskDescription4', 'NEW', 4, now(), now()),
       (2, 'Task5', 'TaskDescription5', 'IN_PROGRESS', 5, now(), now());
