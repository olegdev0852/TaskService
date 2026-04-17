--liquibase formatted sql

--changeset oleg.dev:002-add-colum-authorId
alter table tasks add column author_id uuid;


