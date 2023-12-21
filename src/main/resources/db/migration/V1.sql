create table if not exists subscriber
(
    id              bigserial primary key,
    bot_state       varchar(50),
    first_name      varchar(100),
    deleted         boolean   not null,
    registration    timestamp not null,
    activity        timestamp
);

create table if not exists equipment
(
    id                bigserial primary key,
    id_verification   varchar(50),
    mit_number        varchar(100),
    number            varchar(50)  not null,
    valid_date        date         not null,
    verification_date date         not null,
    name              varchar(200) not null,
    modification      varchar(200),
    org_title         varchar(100),
    result            varchar(20),
    user_id           bigserial,
    href              varchar(200)
);

create table if not exists email
(
    id      bigserial primary key,
    email   varchar(50),
    user_id bigserial
);