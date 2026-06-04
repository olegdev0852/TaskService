--liquibase formatted sql

--changeset oleg.dev:003-add-task-fields
alter table tasks
    add column state       varchar(50) default 'CREATED',
    add column no_test     boolean     default false,
    add column tech_task   boolean     default false,
    add column deadline    timestamp,
    add column assigned_to uuid;

-- rollback
--ALTER TABLE tasks DROP COLUMN state, DROP COLUMN no_test,DROP COLUMN tech_task,DROP COLUMN deadline,DROP COLUMN assigned_to;