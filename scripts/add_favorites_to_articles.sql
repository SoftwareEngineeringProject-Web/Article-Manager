use blog;
alter table articles add column favorites bigint not null default 0;