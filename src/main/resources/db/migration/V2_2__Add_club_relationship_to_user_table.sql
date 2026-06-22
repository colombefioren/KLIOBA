create table club_membership
(
    club_id varchar not null,
    user_id varchar not null
);

alter table club_membership
    add constraint fk_clumem_on_j_club foreign key (club_id) references club (id);

alter table club_membership
    add constraint fk_clumem_on_j_user foreign key (user_id) references "user" (id);