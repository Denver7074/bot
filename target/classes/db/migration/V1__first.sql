create table mailing (
    id bigint not null, email varchar(255),
    notification bit not null,
    user_user_id bigint,
    primary key (id))

create table mailing_seq (
    next_val bigint)
insert into mailing_seq values ( 1 )
create table user (
    user_id bigint not null,
    activity datetime(6),
    bot_state smallint,
    count_si integer not null,
    day_mailing integer not null,
    name varchar(255),
    primary key (user_id))
create table verification (
    id_verification varchar(255) not null,
    applicability bit,
    href varchar(255),
    mi_type varchar(255),
    mit_number varchar(255),
    number varchar(255),
    org_title varchar(255),
    valid_date date,
    verification_date date,
    writing_about_verification date,
    user_user_id bigint,
    primary key (id_verification))
alter table mailing add constraint FKodjhf5oiro14uwcbbu6ikcpif foreign key (user_user_id) references user (user_id)
alter table verification add constraint FK52cm4hpljrmyndmwc4wxecr1l foreign key (user_user_id) references user (user_id)