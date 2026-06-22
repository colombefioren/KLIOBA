create table club
(
    id         varchar
        constraint club_pk primary key,
    name varchar unique not null
);