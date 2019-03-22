drop table if exists task;
drop table if exists project;

create table project
(
  id          serial primary key,
  title       varchar   not null unique,
  description text,
  created     timestamp not null,
  modified    timestamp not null,
  deleted     boolean not null default false
);

create table task
(
  id          serial primary key,
  project_id  integer     not null,
  title       varchar     not null,
  description text,
  status      varchar(16) not null,
  priority    integer,
  created     timestamp   not null,
  modified    timestamp   not null,
  deleted     boolean     not null default false,
  foreign key (project_id) references project (id)
);